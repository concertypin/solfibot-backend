[한국어 문서도 있어요!](https://github.com/konfani/solfibot-backend/blob/master/README_ko.md)

# How to run?

## Requirements

- Windows or Ubuntu 18+
- Python 3.9+
- [Firestore](https://firebase.google.com/products/firestore) and its admin credential
- 8000 port which is not preoccupied by other process. Also, this port can be reached by frontend.
- some environs

  - TWITCH_ACCESS_TOKEN : Twitch user token with `user:read:email+chat:read+chat:edit+moderator:manage:banned_users` scope(it can be generated from [here](https://twitchtokengenerator.com/quick/pKhk2koNES)).

  - FIREBASE_CREDENTIAL : Base64ed content of Firebase admin credential JSON file.
  - TWITCH_CLIENT_ID : Twitch token's client id. This is optional. Default value is `gp762nuuoqcoxypju8c569th9wz7q5`. (client id of [twitchtokengenerator.com]())
  - PREFIX : Prefix of internal functions. If lenght of PREFIX is not 1(or non-ascii character), this might not run properly.
  - TRUSTABLE_USER : Comma-seperated twitch user login ID whose channel will be joined by the bot. this user can access to all of commands and server resource (via ...AsDev command). This is optional. If there isn't value, only moderators can use admin commands.
  - SAFETYBROWSING_KEY : Google Safe Browsing API key.
  - DEV : Optional. If it is 1, frontend connection layer will be disabled.
  - MAX_BONK : Limit `?룰렛` command. If it isn't set, `?룰렛` limitation will be disabled. Else `?룰렛` will be unusabled if a user was timeouted MAX_BONK times.

## Standalone

For Debian or Ubuntu:

1. Clone this repo with `git clone https://github.com/konfani/solfibot-backend && cd solfibot-backend` .
2. Set environment variables. It can also be passed with .env file in repo's root directory.
3. Install require dependencies with `pip install -r requirements.txt` . Using virtual environment is strongly recommend.
4. Run src/main.py with `python3 src/main.py` .

## Docker

```
git clone https://github.com/konfani/solfibot-backend && cd solfibot-backend && docker build -t solfibotbackend:latest

docker run -it -e TWITCH_ACCESS_TOKEN=<YOUR_TOKEN> -e FIREBASE_CREDENTIAL=<YOUR_CREDENTIAL> -e PREFIX=<PREFIX> -e TARGET=<TARGET> -e SAFETYBROWSING_KEY=<SAFETYBROWSING_KEY> -e TRUSTABLE_USER=<TRUSTABLE_USER> -p 8000:8000 solfibotbackend
```

Environment variables can be passed with both .env file or Docker parameters.

# How to use?

- Default command

  - Every default command should be called with prefix passed by `PREFIX` environ.

  - `등록 <command> <response>` : If new chat is `<command>`, then send `response` in that channel. Commands are saved in seperated channel(inter-channel command sharing is not available). If `<command>` or `<response>` contains space, it should be covered with baptik( \` ).
  - `삭제 <command>` : Delete registed command.
  - `목록` : List registed commands.
  - `echo <string>`: Send `<string>` to that channel. If `<string>` is started with slash(/), normal users can't use it.
  - `evalAsDev <expession>` : Return the result of `<expression>`. Only trustable users(written in environs) can run it.
  - `execAsDev <cmd>` : Execute `<cmd>` in Python. Only trustable users(written in environs) can run it.
  - `ping` : Return `?나임?`. Everyone can run this command.
  - `링크검열` : Toggle threat link protection. Default is disabled.
  - `룰렛` : Five-seconds timeout. If succeed(⅙ odds), return `탕!` and timeout the message's author in `러시안 룰렛당해버린` reason. If failed(⅚ odds), return `찰캌` instead of timeout.