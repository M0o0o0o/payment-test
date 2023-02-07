FROM openjdk:11 as builder
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM openjdk:11
COPY --from=builder build/libs/*.jar app.jar
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar", "-Dspring-boot.run.arguments=--key=${KEY}, --secret=${SECRET}"]
#docker run -p 8080:8080 [도커이미지명] --DATASOURCE_USERNAME=kangsh --DATASOURCE_PASSWORD=kangsh12
