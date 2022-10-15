from twitchio.ext import commands
from modules import twitch
from modules import firebase
from settings import is_trustable, prefix


async def add(ctx: commands.Context):
    if not is_trustable(ctx):
        return

    try:
        username = str(ctx.message.content).split(" ")[1]
        score_offset = int(str(ctx.message.content).split(" ")[2])
        channel_uid = twitch.username_to_uid(ctx.channel.name)
        uid = twitch.username_to_uid(username)

        score = firebase.get_score(uid, channel_uid) + score_offset
    except:
        await ctx.send(f"...뭐라고요? 문법은 {prefix}add <유저ID> <점수> 식이에요.")
        return

    try:
        firebase.write_score(uid, channel_uid, score, username)
    except:
        await ctx.send("...왜 버그가 났죠? 어? 어어?")
        return
    await ctx.send(f"이제 {username}님의 학점은 {score}이에요!")