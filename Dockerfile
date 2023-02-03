FROM gradle:jdk15 AS build
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon



FROM openjdk:15

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/ /app/
WORKDIR /app/

ENV DOCKER=1
ENV SAFE_BROWSING=""

ENTRYPOINT java -Dapi.key=${SAFE_BROWSING} -jar /app/MainKt-all.jar