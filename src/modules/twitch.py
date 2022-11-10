import requests
from settings import token, client_id, botid
import copy
import json


def ban(uid: int, channel_id: int, timeout: int, reason: str = ""):
    # if timeout==0, uid will be banned.
    # if timeout==1, uid will be unbanned.
    # if not, uid will be timeout.
    endpoint = f"https://api.twitch.tv/helix/moderation/bans?broadcaster_id={channel_id}&moderator_id={botid}"
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

    r = requests.post(endpoint, headers=head, data=json.dumps(body))
    if not r.ok:
        print(r.status_code)
        print(r.text)
    return r.json()


def uid_to_username(uid: int) -> str:
    endpoint = f"https://api.twitch.tv/helix/users?id={uid}"
    head = {"Authorization": f"Bearer {token}", "Client-Id": client_id}

    r = requests.get(endpoint, headers=head).json()
    return r["data"][0]["login"]


def uid_to_nickname(uid: int) -> str:
    endpoint = f"https://api.twitch.tv/helix/users?id={uid}"
    head = {"Authorization": f"Bearer {token}", "Client-Id": client_id}

    r = requests.get(endpoint, headers=head).json()
    return r["data"][0]["display_name"]


def username_to_uid(username: str) -> int:
    endpoint = f"https://api.twitch.tv/helix/users?login={username}"
    head = {"Authorization": f"Bearer {token}", "Client-Id": client_id}

    r = requests.get(endpoint, headers=head).json()
    return r["data"][0]["id"]
