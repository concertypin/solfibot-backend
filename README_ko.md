실행법
===

요구사항
---
- Windows 또는 Ubuntu 18 이상
- Python 3.6 이상
- [Firestore](https://firebase.google.com/products/firestore)와 Admin SDK JSON 파일

- 환경변수
    - TWITCH_ACCESS_TOKEN : 트위치 토큰. Scope로 `user:read:email+chat:read+chat:edit` 가 있어야 합니다. ([이곳에서](https://twitchtokengenerator.com/quick/pKhk2koNES) 생성할 수 있습니다.)

    - FIREBASE_CREDENTIAL : Base64 인코딩된 Firebase Admin SDK JSON 파일
    - TWITCH_CLIENT_ID : Twitch 토큰의 Client ID. 생략할 수 있습니다. 기본값은 `gp762nuuoqcoxypju8c569th9wz7q5` 입니다. ([twitchtokengenerator.com]()의 기본 토큰)
    - PREFIX : 기본 명령어 호출 접두사. 두 글자 이상이거나 영어/특수문자가 아니라면 정상 작동하지 않을 수 있습니다.
    - TRUSTABLE_USER : 콤마(`,`)로 구분된 Twitch 유저 ID. 이곳에 쓰인 유저는 봇의 완전한 권한을 얻습니다(...AsDev 명령어 포함). 생략할 수 있습니다. 주어지지 않았다면, 채널의 관리자만 관리 명령어를 사용할 수 있습니다.
    - SAFETYBROWSING_KEY : Google Safe Browsing API 키.

Standalone
---
Debian 또는 Ubuntu:
1. `git clone https://github.com/konfani/solfibot-backend && cd solfibot-backend`로 레포를 클론하고 디렉터리로 이동합니다.
2. 환경 변수를 설정합니다. 현재 디렉터리에 있는 .env 파일로도 설정 가능합니다.
3. `pip install -r requirements.txt`를 이용해 종속성을 설치합니다. 실행 시 가상 환경 사용을 강력히 권장합니다.
4. `python3 src/main.py`로 프로그램을 실행합니다.

Docker
---
```
git clone https://github.com/konfani/solfibot-backend && cd solfibot-backend && docker build -t solfibotbackend:latest

docker run -it -e TWITCH_ACCESS_TOKEN=<YOUR_TOKEN> -e FIREBASE_CREDENTIAL=<YOUR_CREDENTIAL> -e PREFIX=<PREFIX> -e TARGET=<TARGET> -e SAFETYBROWSING_KEY=<SAFETYBROWSING_KEY> -e TRUSTABLE_USER=<TRUSTABLE_USER> -p 8000:8000 solfibotbackend
```
환경 변수는 .env file이나 Docker 옵션으로 넘길 수 있습니다.

실행법
===
- 기본 명령어
    - 모든 기본 명령어는 `PREFIX` 환경 변수로 지정된 접두사와 같이 입력되어야 합니다.

    - `등록 <command> <response>` : `<command>` 채팅이 들어오면, `<response>` 채팅을 그 채널에 보냅니다. 명령어는 각 채널에 보관됩니다. (채널 간 공유는 지원되지 않습니다.) 만약 `<command>`나 `<response>`가 공백을 포함한다면, 뱁틱( \` )으로 감싸야 합니다.
    - `삭제 <command>` : 명령어를 삭제합니다.
    - `목록` : 명령어 목록을 출력합니다.
    - `echo <string>`: `<string>`을 채널에 보냅니다. 일반 유저는 `<string>`이 슬래시(/)로 시작한다면 보낼 수 없습니다.
    - `evalAsDev <expession>` : `<expression>`의 결과를 출력합니다. TRUSTABLE_USER만 이 명령어를 실행할 수 있습니다.
    - `execAsDev <cmd>` : 파이썬에서 `<cmd>`를 실행합니다. TRUSTABLE_USER만 이 명령어를 실행할 수 있습니다.
    - `ping` : `?나임?`을 출력합니다. 이 명령어는 모두 사용할 수 있습니다.
    - `링크검열` : 위험한 링크 경고를 토글합니다. 기본값은 경고하지 않습니다.
    - `룰렛` : 6분의 1 확률로 `탕!` 메세지를 출력하고 전송자를 5초 동안 `러시안 룰렛당해버린`이라는 이유로 타임아웃합니다.