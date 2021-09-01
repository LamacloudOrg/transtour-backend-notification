FROM openjdk:8-jdk-alpine
WORKDIR /opt/app
COPY emailv1.html  /opt/app/emailv1.html
COPY logo.png  /opt/app/logo.png
COPY build/libs/*.jar  /opt/app/app.jar
ENTRYPOINT ["java","-Djava.net.preferIPv4Stack=true","-jar","/opt/app/app.jar"]