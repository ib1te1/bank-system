FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY ./deal/pom.xml ./pom.xml
RUN mvn -B dependency:go-offline

COPY ./statement ./
RUN mvn -B package

FROM eclipse-temurin:21-jre
WORKDIR /app

ARG JAR_FILE=statement-0.0.1-SNAPSHOT.jar

COPY --from=builder /app/target/${JAR_FILE} /app/app.jar

COPY ./docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

ENTRYPOINT ["sh", "/usr/local/bin/docker-entrypoint.sh"]
