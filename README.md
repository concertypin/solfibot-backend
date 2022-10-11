    
How to run?
===
Requirements
---
- Ubuntu 18+
    - Windows is also OK, but Windows might be unsupported soon.
- Python 3.6+
- [Firestore](https://firebase.google.com/products/firestore) and its admin credential

- some environs
    - TWITCH_ACCESS_TOKEN : Twitch user token with `user:read:email+chat:read+chat:edit` scope(it can be generated from [here](https://twitchtokengenerator.com/quick/o4qKOhbSmI)).

    - FIREBASE_CREDENTIAL : Base64ed content of Firebase admin credential JSON file.
    - TWITCH_CLIENT_ID : Twitch token's client id. This is optional. Default value is `gp762nuuoqcoxypju8c569th9wz7q5`. (client id of [twitchtokengenerator.com]())
    - PREFIX : Prefix of internal functions. If lenght of PREFIX is not 1(or non-ascii character), this might not run properly.
    - TRUSTABLE_USER : Comma-seperated twitch user login ID. this user can access to all of commands and server resource (via ...AsDev command). This is optional.

Standalone
---
For Debian or Ubuntu:
1. Clone this repo with `git clone https://github.com/konfani/schoolScore && cd schoolScore` .
2. Set environment variables. It can also be passed with .env file in repo's root directory.
3. Install require dependencies with `pip install -r requirements.txt` . Using virtual environment is strongly recommend.
4. Run src/main.py with `python3 src/main.py` .

Docker
---
```
git clone https://github.com/konfani/schoolScore && cd schoolScore && docker build -t schoolscore:latest

docker run -it -e TWITCH_ACCESS_TOKEN=<YOUR_TOKEN> -e FIREBASE_CREDENTIAL=<YOUR_CREDENTIAL> schoolscore
```
Environment variables can be passed with both .env file or Docker parameters.