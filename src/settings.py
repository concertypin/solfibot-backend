import json
import os
from base64 import b64decode

import dotenv
import firebase_admin
import requests
from firebase_admin import credentials, firestore

# init
dotenv.load_dotenv(verbose=True)

# main.py
prefix = os.environ["PREFIX"]

trustable = os.environ.get("TRUSTABLE_USER")
if trustable is None:
    trustable = []
else:
    trustable = trustable.split(",")


def is_trustable(ctx):
    if ctx.author.is_mod:
        return True
    if ctx.author.name in trustable:
        return True
    return False


# firebase.py
app = firebase_admin.initialize_app(
    credentials.Certificate(
        json.loads(b64decode(os.environ["FIREBASE_CREDENTIAL"]).decode())
    )
)
db = firestore.client()

max_bonk=os.environ.get("MAX_BONK")

# twitch.py
token = os.environ["TWITCH_ACCESS_TOKEN"]
client_id = os.environ.get("TWITCH_CLIENT_ID")
botname = requests.get(
    "https://id.twitch.tv/oauth2/validate",
    headers={"Authorization": f"Bearer {token}"},
    timeout=5,
).json()["login"]
botid = requests.get(
    "https://id.twitch.tv/oauth2/validate",
    headers={"Authorization": f"Bearer {token}"},
    timeout=5,
).json()["user_id"]
if client_id is None:
    client_id = "gp762nuuoqcoxypju8c569th9wz7q5"

# safetybrowsing.py
safebrowsing_apikey = os.environ["SAFETYBROWSING_KEY"]
