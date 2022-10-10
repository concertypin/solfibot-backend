import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import os
import json
from base64 import b64decode

cred = credentials.Certificate(json.loads(b64decode(os.environ["FIREBASE_CREDENTIAL"])))

firebase_admin.initialize_app(cred, {
    'databaseURL': os.environ["FIREBASE_URL"]
})


def get_score(uid: int) -> int:
    try:
        ref = db.reference(f'/data/{uid}').get()
        if (ref is None): # user is void
            return 0
        return ref["score"]
    except:
        return 0 #user hasn't been scored


def write_score(uid: int, score: int, username:str="__idk__"):
    ref = db.reference(f"/data/{uid}")
    ref.set({"score":score,"name":username})