FROM gradle:7.6-jdk17 AS build
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon



FROM openjdk:17

RUN mkdir /app
ENV DOCKER=1
COPY --from=build /home/gradle/src/build/libs/ /app/

ENTRYPOINT ["java","-jar","/app/MainKt-all.jar"]