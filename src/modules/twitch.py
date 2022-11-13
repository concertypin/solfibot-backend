import copy
import json
from numba import jit

import requests

from settings import botid, client_id, token


def ban(uid: int, channel_id: int, timeout: int, reason: str = ""):
    # if timeout==0, uid will be banned.
    # if timeout==1, uid will be unbanned.
    # if not, uid will be timeout.
    endpoint = "https://api.twitch.tv/helix/moderation/bans"
    head = {
        "Authorization": f"Bearer {token}",
        "Client-Id": client_id,
        "Content-Type": "application/json",
    }

    body = {"user_id": str(uid)}
    if timeout != 0:
        body["duration"] = str(timeout)
    if reason != "":
        body["reason"] = reason
    ##WIP todo------------------------------------------
    body["data"] = copy.deepcopy(body)

    response = requests.post(
        endpoint,
        headers=head,
        data=json.dumps(body),
        params={"broadcaster_id": channel_id, "moderator_id": botid},
        timeout=5,
    )

    if not response.ok:
        print(response.status_code)
        print(response.text)
    return response.json()


@jit(cache=True)
def uid_to_username(uid: int) -> str:
    endpoint = f"https://api.twitch.tv/helix/users?id={uid}"
    head = {"Authorization": f"Bearer {token}", "Client-Id": client_id}

    response = requests.get(endpoint, headers=head, timeout=5).json()
    return response["data"][0]["login"]


@jit(cache=True)
def uid_to_nickname(uid: int) -> str:
    endpoint = f"https://api.twitch.tv/helix/users?id={uid}"
    head = {"Authorization": f"Bearer {token}", "Client-Id": client_id}

    response = requests.get(endpoint, headers=head, timeout=5).json()
    return response["data"][0]["display_name"]


@jit(cache=True)
def username_to_uid(username: str) -> int:
    endpoint = f"https://api.twitch.tv/helix/users?login={username}"
    head = {"Authorization": f"Bearer {token}", "Client-Id": client_id}

    response = requests.get(endpoint, headers=head, timeout=5).json()
    return response["data"][0]["id"]
