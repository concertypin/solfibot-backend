How to run?
===

Reqiurements
---
- Docker Compose
- Environment variables(via -e options or Docker Compose) or Docker Secret
    - `TWITCH_ACCESS_TOKEN` : [Twitch token](https://twitchtokengenerator.com/quick/qONuuotkyB) (required scope: `user:read:email+chat:read+chat:edit+moderator:manage:banned_users+moderation:read`)
    - `TWITCH_CLIENT_ID` : Client ID of Twitch token. Default value is `gp762nuuoqcoxypju8c569th9wz7q5`
     (Client ID of [twitchtokengenerator.com]())
    - `PREFIX` : Basic command calling prefix.
    - `TRUSTABLE_USER` : comma-seperated Twitch user login ID. These users get every permission of the bot. Optional.
    - `SAFE_BROWSING` : Google Safe Browsing API key.
    - `MAX_CHANCE` : Limit of roulette chance. Default value is 9999. If a user was timeouted MAX_CHANCE times, can't
      call `룰렛` anymore.
    - `DB_PATH` : Path of DB. Empty directory is recommended.

실행법
---
- Fill `<>` in `docker-compose.example.yml`, and rename it to `docker-compose.yml`.
- run with `docker compose up`.

- Basic command
    - Every basic command should be started with `PREFIX`.
    - `등록 <command> <response>` : Register custom command that autoresponse `<response>` when `<command>` is detected in the chat. Command is registered per channel. If `<command>` or `<response>` includes spaces, it should be covered with backtick( \` ).
    - `삭제 <command>` : Delete custom command.
    - `목록` : List custom command.
    - `룰렛` : 1 in 6 chance to print `탕!`, and timeout caller 10 seconds with reason `러시안 룰렛당해버린`.
    - `학점 [user] [offset]`
        - Without `user` and `offset` or Called by non-moderator(both channel-specicated user and TRUSTABLE_USER), print oneself's score.
        - With `user` only, print user's score.
        - With both `user` and `offset`, add `offset` to user's score.