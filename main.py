import os
from dotenv import load_dotenv

load_dotenv(verbose=True)

from twitchio.ext import commands
import twitch

import firebase

prefix = '?'


class Bot(commands.Bot):

    def __init__(self):
        # Initialise our Bot with our access token, prefix and a list of channels to join on boot...
        # prefix can be a callable, which returns a list of strings or a string...
        # initial_channels can also be a callable which returns a list of strings...
        super().__init__(token=os.environ["TWITCH_ACCESS_TOKEN"], prefix=prefix,
                         initial_channels=["orrrchan","badacredit"])

    async def event_ready(self):
        # Notify us when everything is ready!
        # We are logged in and ready to chat and use commands...
        print(f'Logged in as | {self.nick}')
        print(f'User id is | {self.user_id}')
    
    async def event_message(self, message):
        if message.echo:
            return
        await self.handle_commands(message)

    @commands.command()
    async def echo(self,ctx):
        if(ctx.author.is_mod or ctx.author.name =="konfani"):
            await ctx.send(ctx.message.content[6:])

    @commands.command()
    async def 춘추(self,ctx):
        await ctx.send("늙고 병든 1842년생 회장")
    @commands.command()
    async def add(self, ctx: commands.Context):
        """
        ctx.author.name = sender's login id
        ctx.message.content = message string
        """
        if (not(ctx.author.is_mod or ctx.author.name=="konfani")):
            return
        print(ctx.message.content)

        try:
            username = str(ctx.message.content).split(" ")[1]
            score_offset = int(str(ctx.message.content).split(" ")[2])

            uid = twitch.username_to_uid(username)
            score = firebase.get_score(uid) + score_offset
        except:
            await ctx.send(f"...뭐라고요? 문법은 {prefix}add <유저ID> <점수> 식이에요.")
            return

        try:
            firebase.write_score(uid, score,username)
        except:
            await ctx.send("...왜 버그가 났죠? 어? 어어?")
            return

        await ctx.send(f'이제 {twitch.uid_to_nickname(uid)}님의 학점은 {score}이에요!')
    @commands.command()
    async def evalAsDev(self,ctx):
        if(ctx.author.name!="konfani"):
            return
        try:
            a=eval(ctx.message.content[11:])
            if(a is not None):
                await ctx.send(str(a))
            else:
                await ctx.send("eval completed, but returned nothing.")
        except Exception as e:
            await ctx.send(str(e))
    
    @commands.command()
    async def execAsDev(self,ctx):
        if(ctx.author.name!="konfani"):
            return
        try:
            a=exec(ctx.message.content[11:])
            if(a is not None):
                await ctx.send(str(a))
            else:
                await ctx.send("exec completed.")
        except Exception as e:
            await ctx.send(str(e))
    @commands.command()
    async def ping(self, ctx: commands.Context):
        await ctx.send("?나임?")

bot = Bot()
bot.run()
# bot.run() is blocking and will stop execution of any below code here until stopped or closed.