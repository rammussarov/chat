# Chat app
[![CircleCI](https://circleci.com/gh/rammussarov/chat.svg?style=svg)](https://circleci.com/gh/rammussarov/chat)
Simple chat with Spring Boot, RabbitMQ and WebSockets. 

# Install
The instruction assumes that you have already installed [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/).

First of all clone the project to your computer.
```
git clone https://github.com/rammussarov/chat.git
```

Navigate to the directory in which you cloned the project. Create an executable jar. You can run goals like this for the Unix system:
```
./mvnw clean package
```
And the following command for Batch:
```
./mvnw.cmd clean package
```
Build Docker image and start the container.

```
docker-compose build & docker-compose up -d
```
To show list of services use:
```
docker-compose ps
```
To start chatting you need to open the following link in two browsers, or open one in a regular browser window and one in an incognito window.
```
localhost:9090
```
To stop containers, and remove containers and images you can use:
```
docker-compose down
```
