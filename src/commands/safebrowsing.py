from pysafebrowsing import SafeBrowsing
from settings import safebrowsing_apikey
from twitchio.ext import commands
import re

s = SafeBrowsing(safebrowsing_apikey)
trustable_cache_url = ["youtube.com", "youtu.be", "twip.kr", "toon.at"]
trustable_cache_regex = re.compile(
    "("
    + "|".join(list(map(lambda x: str(x).replace(".", r"\."), trustable_cache_url)))
    + ")"
)
url_regex = re.compile(
    r"([\w_-]+(?:(?:\.[\w_-]+)+))([\w.,@?^=%&:/~+#-]*[\w@?^=%&/~+#-])?"
)


def is_url(chat: str):
    t = url_regex.search(chat)
    if t is None:
        return None

    r = []
    for i in chat.split(" "):
        t = url_regex.search(i)
        if t is None:
            continue
        r.append(i[t.start() : t.end()])
    return r


async def lookup(target_url: str):
    t = s.lookup_url(target_url)
    if t["malicious"]:  # danger url
        return t["threats"]
    return None


async def safebrowsing(msg,sender):
    t = is_url(msg)
    if t is None:
        return
    for i in t:
        m=await lookup(i)
        if m is not None:
            await sender(f"위험한 사이트 같아요! 조심하세요! {m} 사이트라고 보고되었어요!")
            return
