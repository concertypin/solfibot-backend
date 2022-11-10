import uvicorn
from fastapi import FastAPI

app = FastAPI()
join_channels = []


@app.get("/join")
async def join(username: str):
    join_channels.append(username)
    return username


def init(joinch):
    global join_channels
    join_channels = joinch
    print("ipc server is starting:")
    uvicorn.run(app, host="0.0.0.0", port=8000)
