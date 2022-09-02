import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import os

# Fetch the service account key JSON file contents
cred = credentials.Certificate(os.environ["FIREBASE_CREDENTIAL"])

# Initialize the app with a service account, granting admin privileges
firebase_admin.initialize_app(cred, {
    'databaseURL': os.environ["FIREBASE_URL"]
})


# As an admin, the app has access to read and write all data, regradless of Security Rules
def get_score(uid: int) -> int:
    try:
        ref = db.reference(f'/data/{uid}').get()
        if (ref is None):
            return 0
        return ref
    except:
        return 0


def write_score(uid: int, score: int):
    ref = db.reference(f"/data/{uid}")
    ref.set(score)
