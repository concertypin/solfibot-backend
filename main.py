import os

from twitchio.ext import commands
import twitch

import firebase

prefix = '?'


class Bot(commands.Bot):

    def __init__(self):
        # Initialise our Bot with our access token, prefix and a list of channels to join on boot...
        # prefix can be a callable, which returns a list of strings or a string...
        # initial_channels can also be a callable which returns a list of strings...
        super().__init__(token=os.environ["TWITCH_ACCESS_TOKEN"], prefix=prefix, initial_channels=['orrrchan'])

    async def event_ready(self):
        # Notify us when everything is ready!
        # We are logged in and ready to chat and use commands...
        print(f'Logged in as | {self.nick}')
        print(f'User id is | {self.user_id}')

    @commands.command()
    async def add(self, ctx: commands.Context):
        # Here we have a command hello, we can invoke our command with our prefix and command name
        # e.g ?hello
        # We can also give our commands aliases (different names) to invoke with.

        # Send a hello back!
        # Sending a reply back to the channel is easy... Below is an example.

        """
        ctx.author.name = sender's login id
        ctx.message.content = message string
        """
        if (not ctx.author.is_mod):
            return
        print(ctx.message.content)

        try:
            username = str(ctx.message.content).split(" ")[1]
            score_offset = int(str(ctx.message.content).split(" ")[2])

            print(username, score_offset)

            uid = twitch.username_to_uid(username)
            score = firebase.get_score(uid) + score_offset
        except:
            await ctx.send(f"...뭐라고요? 문법은 {prefix}add <유저ID> <점수> 식이에요.")
            return

        try:
            firebase.write_score(uid, score)
        except:
            await ctx.send("...왜 버그가 났죠? 어? 어어?")
            return

        await ctx.send(f'이제 {twitch.uid_to_nickname(uid)}님의 학점은 {score}이에요!')


bot = Bot()
bot.run()
# bot.run() is blocking and will stop execution of any below code here until stopped or closed.
