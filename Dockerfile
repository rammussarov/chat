FROM openjdk:8-jdk-alpine

RUN apk update && apk add bash

EXPOSE 9090

WORKDIR /usr/app

ADD ./wait-for-it.sh /usr/app/wait-for-it.sh
RUN chmod +x /usr/app/wait-for-it.sh

RUN addgroup -S chatApp && adduser -S chatApp -G chatApp
USER chatApp:chatApp

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /usr/app/application.jar

ENV RABBIT_HOST=rabbit

ENTRYPOINT ["/usr/app/wait-for-it.sh", "rabbit:61613", "--", "java","-jar", "/usr/app/application.jar"]

