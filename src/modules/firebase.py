from settings import db


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
