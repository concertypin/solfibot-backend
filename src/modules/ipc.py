import uvicorn
from fastapi import FastAPI

app = FastAPI()
join_channels=None

@app.get("/join")
async def join(username:str):
    await join_channels([username])

def init(joinch):
    join_channels=joinch[0]
    print("ipc server is starting:")
    uvicorn.run(app, host="0.0.0.0", port=8000)
