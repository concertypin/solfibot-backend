from pysafebrowsing import SafeBrowsing
from settings import safebrowsing_apikey
import asyncio

s = SafeBrowsing(safebrowsing_apikey)


async def lookup(target_url: str) -> str:
    t = s.lookup_url(target_url)
    if t["malicious"]:  # danger url
        return t["threats"]
    return None

