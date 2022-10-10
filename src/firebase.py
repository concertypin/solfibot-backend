import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from base64 import b64decode
import json
import os
cred = credentials.Certificate(json.loads(b64decode(os.environ["FIREBASE_CREDENTIAL"]).decode()))
app = firebase_admin.initialize_app(cred)
db = firestore.client()

# As an admin, the app has access to read and write all data, regradless of Security Rules
def get_score(uid: int, channel_uid:int) -> int:
    ref = db.collection(u"listener_data").document(str(uid)).get().to_dict()

    if (ref is None): # no such user
        return 0
    
    try:
        return ref["score"][channel_uid]
    except KeyError:
        return 0 # user hasn't been scored in that channel


def write_score(uid: int, channel_uid:int, score: int, username:str="__idk__"):
    ref = db.reference(f"/data/{uid}")
    ref.set({"score":score,"name":username})