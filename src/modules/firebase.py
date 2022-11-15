import time

from google.cloud import firestore

from settings import db

max_bonk=2

def get_combo(uid: int) -> int:
    ref = db.collection("listener_data").document(str(uid)).get().to_dict()

    if ref is None:  # no such user
        return 0

    if ref.get("roulette_combo") is None:
        return 0

    return ref.get("roulette_combo")


def is_roulettable(uid: int) -> bool:
    ref = db.collection("listener_data").document(str(uid)).get().to_dict()

    now_bonk = ref.get("now_bonk")
    if now_bonk is None:
        now_bonk = 0
    return max_bonk >= now_bonk


def set_combo(uid: int, combo: int):
    ref = db.collection("listener_data").document(str(uid))
    data = {"roulette_combo": combo}
    if combo == 0:  # bomb
        remote_data = ref.get().to_dict()
        if remote_data.get("now_bonk") is None:
            now_bonk = 1
        else:
            now_bonk = remote_data["now_bonk"]

        t = time.time()
        if remote_data.get("last_bonk") is None:
            last_bonk = 0
        else:
            last_bonk = remote_data["last_bonk"]

        if t - 60 * 60 * 24 > last_bonk:  # lastBonk.time < yesterday
            now_bonk = 1

        data["last_bonk"] = t
        data["now_bonk"] = now_bonk

    else:
        if is_roulettable(uid) is True:
            raise Exception
        remote_data = ref.get().to_dict()
        if remote_data.get("now_bonk") is None:
            now_bonk = 2
        else:
            now_bonk = remote_data["now_bonk"]+1
        data["now_bonk"]=now_bonk
    
    ref.set(data, merge=True)


def get_score_map(uid: int) -> dict:
    ref = db.collection("listener_data").document(str(uid)).get().to_dict()

    if ref is None:  # no such user
        return {}

    if ref.get("score") is None:
        return {}

    return ref.get("score")


def get_score(uid: int, channel_uid: int) -> int:
    score_map = get_score_map(uid)
    if score_map is {}:
        return 0
    if (
        score_map.get(str(channel_uid)) is None
    ):  # user hasn't been scored in that channel
        return 0
    return score_map.get(str(channel_uid))


def write_score(uid: int, channel_uid: int, score: int, username: str = "__idk__"):
    doc = db.collection("listener_data").document(str(uid))

    score_dict = get_score_map(uid)
    score_dict[str(channel_uid)] = score
    data = {"nickname": username, "score": score_dict, "uid": int(uid)}

    doc.set(data)


def read_commands(channel_uid: int) -> dict:
    ref = db.collection("streamer_data").document(str(channel_uid)).get().to_dict()

    if ref is None:  # no such user
        return {}
    if ref.get("commands") is None:  # no command
        return {}
    return ref["commands"]


def write_command(channel_uid: int, command: str, response: str):
    ref = db.collection("streamer_data").document(str(channel_uid))

    command_dict = read_commands(channel_uid)
    command_dict[command] = response
    data = {"commands": command_dict}
    ref.set(data)


def delete_command(channel_uid: int, command: str):
    ref = db.collection("streamer_data").document(str(channel_uid))

    command_dict = read_commands(channel_uid)
    if command_dict.get(command) is not None:
        del command_dict[command]
        data = {"commands": command_dict}
        ref.set(data)


def is_safesbowsing_enabled(channel_id: int) -> bool:
    ref = db.collection("streamer_data").document(str(channel_id)).get().to_dict()
    if ref is None:
        return False
    if ref.get("safety_browsing") is None:
        return False
    return ref.get("safety_browsing")


def set_safetybrowsing(channel_id: int, stat: bool):
    ref = db.collection("streamer_data").document(str(channel_id))
    ref.set({"safety_browsing": stat}, merge=True)


def highest_lookup(max_result: int = 3) -> dict:
    ref = (
        db.collection("listener_data")
        .order_by("roulette_combo", direction=firestore.Query.DESCENDING)
        .limit(max_result)
    )
    ret = []
    for i in ref.get():
        ret.append([int(i.id), i.to_dict()["roulette_combo"]])  # [int(id), int(value)]
    return ret
