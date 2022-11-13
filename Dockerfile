FROM pypy:3.9

EXPOSE 8000
WORKDIR /home/ubuntu/schoolScore

COPY requirements.txt ./
RUN curl https://sh.rustup.rs -sSf | sh -s -- -y
RUN source "$HOME/.cargo/env"
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD [ "python", "./src/main.py" ]
