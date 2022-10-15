from settings import is_trustable, prefix
from modules import twitch
from modules import firebase
from twitchio.ext import commands


async def register(ctx: commands.Context):
    if not is_trustable(ctx):
        return
    print(ctx.author.name, "->", ctx.message.content)
    msg = str(ctx.message.content)
    error_msg = f"명령어는 '{prefix}등록 등록할명령어 대답할단어' 식이에요. 명령어나 단어 사이에 띄어쓰기가 있다면, 그 명령어나 단어를 ` 문자로 감싸주세요!"

    # parsing
    try:
        baptik_num = len(msg) - len(
            "".join(msg.split("`"))
        )  # how many baptiks in the msg
        if (
            baptik_num != 2 and baptik_num != 4 and baptik_num != 0
        ):  # baptik pair is neither 0, 1 nor 2
            raise Exception

        if (
            baptik_num == 0 and len(msg.split(" ")) != 3
        ):  # it isn't expectable since there is no baptik
            raise Exception

        if baptik_num == 0:  # it is expectable although there is no baptik
            command = msg.split(" ")[1]
            response = msg.split(" ")[2]
        else:
            something_parsed = msg.split("등록 ")[1].split("`")
            command = ""
            response = ""
            for i in something_parsed:
                if i == "" or i == " ":
                    continue
                if command == "":
                    command = i
                else:
                    response = i

    except Exception:
        await ctx.send(error_msg)
        return
    try:
        if command == "" or response == "":
            raise Exception
    except:
        await ctx.send(error_msg)
        return

    # now, cmd is vaild
    firebase.write_command(
        twitch.username_to_uid(ctx.channel.name), command.strip(), response.strip()
    )
    await ctx.send(f"이제 {command.strip()} 채팅에는 {response.strip()} 채팅으로 반응할게요!")


async def delete(ctx: commands.Context):
    if not is_trustable(ctx):
        return
    print(ctx.author.name, "->", ctx.message.content)
    try:
        command = " ".join(ctx.message.content.split(" ")[1:])
    except:
        await ctx.send(f"명령어는 '{prefix}삭제 지울명령어' 식이에요.")
        return
    firebase.delete_command(twitch.username_to_uid(ctx.channel.name), command)
    await ctx.send(f"이제 {command} 채팅에 더 이상 응답하지 않아요!")


async def listing(ctx: commands.Context):
    res = ""
    cmds = firebase.read_commands(twitch.username_to_uid(ctx.channel.name))
    if len(cmds) == 0:
        await ctx.send("등록된 명령어가 없어요.")
        return
    for i in cmds:
        res += i + " | "
    await ctx.send(res[:-3])
