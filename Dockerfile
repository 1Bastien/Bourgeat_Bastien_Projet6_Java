FROM maven:3.9.6-eclipse-temurin-21

WORKDIR /app

COPY target/app-0.0.1-SNAPSHOT.jar app.jar

CMD java -jar app.jar