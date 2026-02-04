FROM gradle:7.6-jdk17 AS builder

WORKDIR /home/gradle/project

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew

RUN ./gradlew clean bootjar

FROM bellsoft/liberica-openjdk-alpine:17

COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

VOLUME /tmp
