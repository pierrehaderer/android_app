FROM openjdk:11-jre-slim

COPY ./build/libs/*.jar /usr/src/app/server.jar
WORKDIR /usr/src/app

CMD java $JAVA_OPTS -jar server.jar
