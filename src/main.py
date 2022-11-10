import sys
import re
import os
from twitchio.ext import commands, routines
import twitchio
from settings import prefix, trustable, is_trustable
from modules import firebase, twitch
from commands import commanding, safebrowsing, scoring, etc
import multiprocessing as mp

trim_paimon = re.compile(" HungryPaimon")
registry_parse = re.compile(r"`[^`]*`")
registry_valid = re.compile(r"등록 `[^`]*` `[^`]*`")
l = mp.Manager().list()

print(f"Prefix is {prefix}")


# noinspection PyPep8Naming,NonAsciiCharacters
class Bot(commands.Bot):
    def __init__(self):
        super().__init__(
            token=os.environ["TWITCH_ACCESS_TOKEN"],
            prefix=prefix,
            initial_channels=list(os.environ["TARGET"].split(",")),
        )

    async def event_ready(self):
        print(f"Logged in as | {self.nick}")
        print(f"User id is | {self.user_id}")
        self.join_lookup.start()

    @staticmethod
    def command_handle(msg: str, channel_name: str):
        channel_uid = twitch.username_to_uid(channel_name)
        commands_dict = firebase.read_commands(channel_uid)

        command = trim_paimon.sub("", msg)  # trimming HungryPaimon emoji
        response = commands_dict.get(command)
        if response is None:
            return
        return response

    async def event_command_error(self, ctx, error):
        # if unable to find response func(or there is REAL ERROR in response func), this func will be executed.
        if str(error).find("No command") == -1:
            print(error, file=sys.stderr)  # print THAT REAL ERROR in stderr
            return
        res = self.command_handle(ctx.message.content, ctx.channel.name)
        if res is not None:
            await ctx.send(res)

    async def event_message(self, message: twitchio.message.Message):
        if message.echo:
            return

        await safebrowsing.safebrowsing(
            message.content,
            message.channel.send,
            twitch.username_to_uid(message.channel.name),
        )
        # first, try handling that message with response function.
        # if failed, event_command_error() will be executed, and it will continue processing.

        if not message.content.startswith(prefix):
            # cause handle_commands ignores msg if msg doesn't start with the prefix, manually call.
            res = self.command_handle(message.content, message.channel.name)
            if res is not None:
                await message.channel.send(res)
                return

        await self.handle_commands(message)

    @commands.command()
    async def 등록(self, ctx):
        await commanding.register(ctx)

    @commands.command()
    async def 삭제(self, ctx):
        await commanding.delete(ctx)

    @commands.command()
    async def 목록(self, ctx):
        await commanding.listing(ctx)

    @commands.command()
    async def echo(self, ctx):
        if is_trustable(ctx) or not ctx.message.content[6:].strip().startswith("/"):
            await ctx.send(ctx.message.content[6:])
            return

    @commands.command()
    async def add(self, ctx: commands.Context):
        await scoring.add(ctx)

    @commands.command()
    async def 링크검열(self, ctx: commands.Context):
        if not is_trustable(ctx):
            return
        uid = twitch.username_to_uid(ctx.channel.name)
        now = firebase.is_safesbowsing_enabled(uid)
        if now:
            firebase.set_safetybrowsing(uid, False)
            await ctx.send("이제 더 이상 위험한 링크를 검사하지 않아요!")
        else:
            firebase.set_safetybrowsing(uid, True)
            await ctx.send("이제부터 위험한 링크를 경고할게요!")

    @commands.command()
    async def evalAsDev(self, ctx):
        if ctx.author.name not in trustable:
            return
        try:
            a = eval(ctx.message.content[11:])
            if a is not None:
                await ctx.send(str(a))
            else:
                await ctx.send("eval completed, but returned nothing.")
        except Exception as e:
            await ctx.send(str(e))

    @commands.command()
    async def execAsDev(self, ctx):
        if ctx.author.name not in trustable:
            return
        try:
            a = exec(ctx.message.content[11:])
            if a is not None:
                await ctx.send(str(a))
            else:
                await ctx.send("exec completed.")
        except Exception as e:
            await ctx.send(str(e))

    @commands.command()
    async def ping(self, ctx: commands.Context):
        await ctx.send("?나임?")

    @commands.command()
    async def 룰렛(self, ctx: commands.Context):
        await etc.russian_roulette(ctx)

    @commands.command()
    async def 리더보드(self, ctx: commands.Context):
        await etc.leaderboard(ctx)

    @routines.routine(seconds=1)
    async def join_lookup(self):
        if len(l) == 0:
            return

        while len(l) != 0:
            await self.join_channels([l[0]])
            print("joined " + l[0])
            del l[0]


if __name__ == "__main__":
    import multiprocessing as mp

    def front():
        bot = Bot()
        bot.run()

    def back():
        from modules import ipc
        import time

        mp.Process(target=ipc.init, args=[l]).start()

    if os.environ.get("DEV") != 1:
        back()
    front()
