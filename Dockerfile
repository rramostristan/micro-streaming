FROM eclipse-temurin:17.0.3_7-jre-alpine
ENV TZ=Europe/Madrid
RUN apk update
WORKDIR /microstreaming
COPY target/micro-streaming-test-SNAPSHOT.jar /microstreaming/app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]