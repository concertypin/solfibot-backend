# Use the official gradle image to create a build artifact.
# https://hub.docker.com/_/gradle
FROM gradle:jdk8 as builder

# just download dependencies
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .
# RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true


# Copy local code to the container image.
COPY . .
# Build a release artifact.
RUN gradle shadowjar -x test --parallel

# Use the Official OpenJDK image for a lean production stage of our multi-stage build.
# https://hub.docker.com/_/openjdk
# https://docs.docker.com/develop/develop-images/multistage-build/#use-multi-stage-builds
FROM openjdk:8-jre-alpine
WORKDIR src
# Copy the jar to the production image from the builder stage.
COPY --from=builder /home/gradle/build/libs/MainKt-all.jar ./main.jar

# Run the web service on container startup.
RUN mkdir db
ENV SAFE_BROWSING=""
CMD java -Dapi.key=${SAFE_BROWSING} -jar main.jar