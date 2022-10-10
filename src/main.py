from dotenv import load_dotenv
load_dotenv(verbose=True)
import os
from twitchio.ext import commands
import twitch
import firebase
<<<<<<< HEAD
import sys
import multiprocessing as mp
import asyncio
import re
=======
from dotenv import load_dotenv
import multiprocessing as mp
import asyncio
>>>>>>> 60b4565 (printing command completed)

trim_paimon=re.compile(" HungryPaimon")
registry_parse=re.compile(r"`[^`]*`")
registry_vaild=re.compile(r"등록 `[^`]*` `[^`]*`")
prefix = '$'
trustable=["konfani"]

def is_trustable(ctx):
    if(ctx.author.is_mod):
        return True
    if(ctx.author.name in trustable):
        return True
    return False

class Bot(commands.Bot):
    def __init__(self):
        super().__init__(token=os.environ["TWITCH_ACCESS_TOKEN"], prefix=prefix,
                         initial_channels=["orrrchan","badacredit"])

    async def event_ready(self):
        print(f'Logged in as | {self.nick}')
        print(f'User id is | {self.user_id}')

<<<<<<< HEAD
    def command_handle(self,msg:str,channel_name:str):
        channel_uid=twitch.username_to_uid(channel_name)
        commands_dict=firebase.read_commands(channel_uid)

        command=trim_paimon.sub("",msg) #trimming HungryPaimon imoji
        response=commands_dict.get(command)
        if(response is None):
            return
        return response

    async def event_command_error(self,ctx,error):
        #if unable to find response func(or there is REAL ERROR in response func), this func will be executed.

        if(str(error).find("No command") == -1):
            print(error,file=sys.stderr) #print THAT REAL ERROR in stderr
            return
        res=self.command_handle(ctx.message.content, ctx.channel.name)
        if(res is not None):
            await ctx.send(res)


    async def event_message(self, message):
        if message.echo:
            return 

        #first, try handling that message with response function.
        #if failed, event_command_error() will be executed, and it will continue processing.

        if(not message.content.startswith(prefix)):
            #cause handle_commands ignores msg if msg doesn't starts with the prefix, manually call.
            res=self.command_handle(message.content, message.channel.name)
            if(res is not None):
                await message.channel.send(res)
        
        await self.handle_commands(message)
=======
    async def event_message(self, message):
        if message.echo:
            return 
        if not message.content.startswith(prefix):
            return
        
        def worker():
            channel_uid=twitch.username_to_uid(message.channel.name)
            commands_dict=firebase.read_commands(channel_uid)

            command=message.content[len(prefix):]
            response=commands_dict.get(command)
            if(response is None):
                return
            
            asyncio.run(message.channel.send(response)) 

        p = mp.Process(name=f"handler of {message.content.split(' ')[0]}", target=worker)
        p.start()
>>>>>>> 60b4565 (printing command completed)

    @commands.command()
    async def 등록(self,ctx):
        if(not is_trustable(ctx)):
            return
        msg=str(ctx.message.content)
        error_msg=f"명령어는 '{prefix}등록 등록할명령어 대답할단어' 식이에요. 명령어나 단어 사이에 띄어쓰기가 있다면, 그 명령어나 단어를 ` 문자로 감싸주세요!"
        try:
            baptik_num=len(msg)-len("".join(msg.split("`")))
            if(baptik_num!=2 and baptik_num!=4 and baptik_num!=0):
                raise Exception
            
            if(baptik_num==0 and len(msg.split(" "))!=2):
                raise Exception
            
            something_parsed=msg.split("등록 ")[1].split("`")
            command=""
            response=""
            for i in something_parsed:
                if(i=="" or i==" "):
                    continue
                if(command==""):
                    command=i
                else:
                    response=i
            
        except Exception as e:
            await ctx.send(error_msg)
            return
        try:
            if command=="" or response=="":
                raise Exception
        except:
            await ctx.send(error_msg)
            return
        
        #now, cmd is vaild
        firebase.write_command(twitch.username_to_uid(ctx.channel.name), command, response)
        await ctx.send(f"이제 {command} 채팅에는 {response} 채팅으로 반응할게요!")

    @commands.command()
    async def 삭제(self,ctx):
        if(not is_trustable(ctx)):
            return
        try:
            command=" ".join(ctx.message.content.split(" ")[1:])
        except:
            await ctx.send(f"명령어는 '{prefix}삭제 지울명령어' 식이에요.")
            return
        firebase.delete_command(twitch.username_to_uid(ctx.channel.name), command)
        await ctx.send(f"이제 {command} 채팅에 더 이상 응답하지 않아요!")
    
    @commands.command()
    async def 목록(self,ctx):   
        res=""
        for i in firebase.read_commands(twitch.username_to_uid(ctx.channel.name)):
            res+=i+" | "
        await ctx.send(res[:-3])

    @commands.command()
    async def echo(self,ctx):
        if(is_trustable(ctx)):
            await ctx.send(ctx.message.content[6:])
    
    @commands.command()
    async def add(self, ctx: commands.Context):
        """
        ctx.author.name = sender's login id
        ctx.channel.name = channel login id
        ctx.message.content = message string
        """
        if(not is_trustable(ctx)):
            return

        try:
            username = str(ctx.message.content).split(" ")[1]
            score_offset = int(str(ctx.message.content).split(" ")[2])
            channel_uid=twitch.username_to_uid(ctx.channel.name)
            uid = ctx.author.id

            score = firebase.get_score(uid,channel_uid) + score_offset
        except:
            await ctx.send(f"...뭐라고요? 문법은 {prefix}add <유저ID> <점수> 식이에요.")
            return

        try:
            firebase.write_score(uid,channel_uid,score,ctx.channel.name)
        except:
            await ctx.send("...왜 버그가 났죠? 어? 어어?")
            return

        await ctx.send(f'이제 {twitch.uid_to_nickname(uid)}님의 학점은 {score}이에요!')
    
    @commands.command()
    async def evalAsDev(self,ctx):
        if(ctx.author.name not in trustable):
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
        if(ctx.author.name not in trustable):
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