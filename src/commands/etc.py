from modules import twitch
from random import randint
from twitchio.ext import commands
from modules import firebase


async def russian_roulette(ctx: commands.Context):
    if randint(1, 6) == 1:
        await ctx.send(f"탕! {firebase.get_combo(ctx.author.id)}번 살아남으셨습니다!")
        firebase.set_combo(ctx.author.id, 0)
        twitch.ban(
            ctx.author.id, twitch.username_to_uid(ctx.channel.name), 10, "러시안 룰렛당해버린"
        )
    else:
        t = firebase.get_combo(ctx.author.id)
        firebase.set_combo(ctx.author.id, t + 1)
        await ctx.send(f"찰캌! {t+1}번 살아남으셨습니다!")


async def leaderboard(ctx: commands.Context):
    command=str(ctx.message.content).strip().split(" ")
    try:
        max_lookup=int(command[1])
    except:
        max_lookup=3
    ret=""
    for i in firebase.highest_lookup(max_lookup):
        ret+=f"{twitch.uid_to_nickname(i[0])}:{i[1]}점, "
    await ctx.send(ret[:-2])