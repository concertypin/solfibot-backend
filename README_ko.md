[English document/영어 문서](README.md)

# 실행법

## 요구사항

- Docker Compose
- .env 파일을 통한 환경 변수
    - `TWITCH_ACCESS_TOKEN` : [트위치 토큰](https://twitchtokengenerator.com/quick/qONuuotkyB) (필수
      스코프:`user:read:email+chat:read+chat:edit+moderator:manage:banned_users+moderation:read`)
    - `TWITCH_CLIENT_ID` : Twitch 토큰의 Client ID. 생략할 수 있습니다. 기본값은 `gp762nuuoqcoxypju8c569th9wz7q5`
      입니다. ([twitchtokengenerator.com]()의 기본 토큰)
    - `PREFIX` : 기본 명령어 호출 접두사.
    - `TRUSTABLE_USER` : 콤마(`,`)로 구분된 Twitch 유저 ID. 이곳에 쓰인 유저는 봇의 완전한 권한을 얻습니다. 생략할 수 있습니다. 주어지지 않았다면, 채널의 관리자만 관리 명령어를
      사용할 수 있습니다.
    - `SAFE_BROWSING` : Google Safe Browsing API 키.
    - `MAX_CHANCE` : 러시안 룰렛의 제한을 설정합니다. 만약 존재하지 않을 경우, 9999로 설정됩니다. 만약 자연수일 경우, 한 유저가 MAX_CHANCE만큼 타임아웃 된 후에는 더 이상 `룰렛`
      명령어를 이용할 수 없습니다.`
    - `DB_PATH` : DB가 저장될 경로. 빈 폴더를 권장합니다.
    - `API_SERVER_PORT` : API 서버를 열 포트. 주어지지 않는다면, API 서버가 열리지 않습니다,

# 명령어

- `example.env` 파일의 `<>`를 채우고, 파일명을 `.env`으로 변경하세요.
- `docker compose up`으로 시작하세요.

# 사용법

## 명령어

- 기본 명령어
    - 모든 기본 명령어는 `PREFIX` 환경 변수로 지정된 접두사와 같이 입력되어야 합니다.

    - `등록 <command> <response>` : `<command>` 채팅이 들어오면, `<response>` 채팅을 그 채널에 보냅니다. 명령어는 각 채널에 보관됩니다. (채널 간 공유는 지원되지
      않습니다.) 만약 `<command>`나 `<response>`가 공백을 포함한다면, 뱁틱( \` )으로 감싸야 합니다.
        - 만약 response에 `;;`이 포함된다면, 출력값은 유저의 권한에 의해 좌우됩니다.
            - 예시로, `a;;b;;c`는 `a`, `b`, `c`로 구분됩니다.
            - 세 번째는 TRUSTABLE_USER 전용입니다.
            - 두 번째는 채널의 브로드캐스터와 TRUSTABLE_USER 전용입니다.
            - 첫 번째는 모든 사용자용입니다.
    - `삭제 <command>` : 명령어를 삭제합니다.
    - `목록` : 명령어 목록을 출력합니다.
    - `룰렛` : 6분의 1 확률로 `탕!` 메세지를 출력하고 전송자를 10초 동안 `러시안 룰렛당해버린`이라는 이유로 타임아웃합니다.
    - `머리수복 <user>` : user의 룰렛 기회를 하나 더 부여합니다.
    - `학점 [user] [offset]`
        - user와 offset 인자 없이 실행하거나 관리자가 아닌 유저가 실행할 경우, 자신의 학점을 출력합니다.
        - 관리자인 유저가 user 인자와 함께 실행할 경우, user의 학점을 출력합니다.
        - 관리자인 유저가 user와 offset 인자와 함께 실행할 경우, user의 학점에 offset을 더합니다.
    - `링크검열` : Google Safe Browsing 경고를 토글합니다.

## API

[영어 문서](README.md)를 참조하세요.