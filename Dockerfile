FROM openjdk:8-jdk-alpine
WORKDIR /opt/app
ENV token-header="AAAAK2hm1uA:APA91bG95gkMBlg3qYndjedmS1jJOLyV4DHOUBf_PQJBpbo6D9JAfWvprPrFEZGyPg48ZdNDulVKCKPVLkY6MHFuYn3mzAWi3kpnF7VQVc8kW4JX1vgq8pAMQAdaYBET8WuV9J3wml2s"
COPY build/libs/*.jar  /opt/app/app.jar
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]