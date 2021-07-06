FROM openjdk:8-jdk-alpine
WORKDIR /opt/app
COPY build/libs/*.jar  /opt/app/app.jar
ENTRYPOINT ["java","-Djava.net.preferIPv4Stack=true","-jar","/opt/app/app.jar"]