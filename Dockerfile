FROM python:3.10

EXPOSE 8000
WORKDIR /home/ubuntu/schoolScore

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD [ "python", "./src/main.py" ]