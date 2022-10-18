import os
import firebase_admin
import json
from base64 import b64decode
from firebase_admin import credentials
from firebase_admin import firestore
import dotenv

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


# twitch.py
token = os.environ["TWITCH_ACCESS_TOKEN"]
client_id = os.environ.get("TWITCH_CLIENT_ID")
if client_id is None:
    client_id = "gp762nuuoqcoxypju8c569th9wz7q5"


# safetybrowsing.py
safebrowsing_apikey = os.environ["SAFETYBROWSING_KEY"]
