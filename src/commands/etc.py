from modules import twitch
from random import randint
from twitchio.ext import commands


async def russian_roulette(ctx: commands.Context):
    if randint(1, 6) == 1:
        await ctx.send("탕!")
        twitch.ban(
            ctx.author.id, twitch.username_to_uid(ctx.channel.name), 10, "러시안 룰렛당해버린"
        )
    else:
        await ctx.send()
