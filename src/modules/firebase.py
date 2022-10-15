from settings import db


def get_score_map(uid: int) -> dict:
    ref = db.collection(u"listener_data").document(str(uid)).get().to_dict()

    if ref is None:  # no such user
        return {}

    return ref["score"]


def get_score(uid: int, channel_uid: int) -> int:
    try:
        score_map = get_score_map(uid)
        if score_map is None:
            return 0
        return score_map[str(channel_uid)]
    except KeyError:
        return 0  # user hasn't been scored in that channel


def write_score(uid: int, channel_uid: int, score: int, username: str = "__idk__"):
    doc = db.collection(u"listener_data").document(str(uid))

    score_dict = get_score_map(uid)
    score_dict[str(channel_uid)] = score
    data = {u"nickname": username, u"score": score_dict, u"uid": int(uid)}

    doc.set(data)


def read_commands(channel_uid: int) -> dict:
    ref = db.collection(u"streamer_data").document(str(channel_uid)).get().to_dict()

    if ref is None:  # no such user
        return {}
    if ref.get("commands") is None:  # no command
        return {}
    return ref["commands"]


def write_command(channel_uid: int, command: str, response: str):
    ref = db.collection(u"streamer_data").document(str(channel_uid))

    command_dict = read_commands(channel_uid)
    command_dict[command] = response
    data = {u"commands": command_dict}
    ref.set(data)


def delete_command(channel_uid: int, command: str):
    ref = db.collection(u"streamer_data").document(str(channel_uid))

    command_dict = read_commands(channel_uid)
    if command_dict.get(command) is not None:
        del command_dict[command]
        data = {u"commands": command_dict}
        ref.set(data)
