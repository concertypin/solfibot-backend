[Korean document/한국어 문서](README_ko.md)

# How to run?

## Reqiurements

- Docker Compose
- Environment variables via .env file
    - `TWITCH_ACCESS_TOKEN` : [Twitch token](https://twitchtokengenerator.com/quick/qONuuotkyB) (required
      scope: `user:read:email+chat:read+chat:edit+moderator:manage:banned_users+moderation:read`)
    - `TWITCH_CLIENT_ID` : Client ID of Twitch token. Default value is `gp762nuuoqcoxypju8c569th9wz7q5`
      (Client ID of [twitchtokengenerator.com](http://twitchtokengenerator.com))
    - `PREFIX` : Basic command calling prefix.
    - `TRUSTABLE_USER` : comma-seperated Twitch user login ID. These users get every permission of the bot. Optional.
    - `SAFE_BROWSING` : Google Safe Browsing API key.
    - `MAX_CHANCE` : Limit of roulette chance. Default value is 9999. If a user was timeouted MAX_CHANCE times, can't
      call `룰렛` anymore.
    - `DB_PATH` : Path of DB. Empty directory is recommended.
    - `API_SERVER_PORT` : Turn on API server in this port. Optional. If it is not given, API server will be disabled.

# Run
- Fill `<>` in `example.env`, and rename it to `.env`.
- run with `docker compose up`.

## Command
- Basic command
    - Every basic command should be started with `PREFIX`.
    - `등록 <command> <response>` : Register custom command that autoresponse `<response>` when `<command>` is detected in the chat. Command is registered per channel. If `<command>` or `<response>` includes spaces, it should be covered with backtick( \` ).
      - If response commands `;;`, response will be seperated by permission.
        - For example, `a;;b;;c` will be seperated by `a`,`b`,`c`.
        - Third one(`c`) is for only sudoers.
        - Second one(`b`) is for channel mods.
        - First one(`a`) is everyone.
        - If seperated command is not enough, it's response will be down one level.
    - `삭제 <command>` : Delete custom command.
    - `목록` : List custom command.
    - `룰렛` : 1 in 6 chance to print `탕!`, and timeout caller 10 seconds with reason `러시안 룰렛당해버린`.
    - `머리수복 <user>` : Increase one chance of `user`.
    - `학점 [user] [offset]`
        - Without `user` and `offset` or Called by non-moderator(both channel-specicated user and TRUSTABLE_USER), print
          oneself's score.
        - With `user` only, print user's score.
        - With both `user` and `offset`, add `offset` to user's score.
    - `링크검열` : Toggle Google Safe Browsing.

## API

Beta. Might be changed.

- URL : `/` (root)
    - GET : List state.

```json lines
//request : no header param or anything is required.
//response :
{
  "joinedUser": [
    //channels which are joined.
    "solfibot"
  ]
}
```

### Channel

- URL : `/channel/{channelName}`
    - POST : Join {channelName}.
    - DELETE : Leave {channelName}.