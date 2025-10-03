FROM openjdk:21-jdk-slim

RUN apt-get update && \
    apt-get install -y tzdata && \
    ln -fs /usr/share/zoneinfo/Europe/Stockholm /etc/localtime && \
    dpkg-reconfigure -f noninteractive tzdata

ENV TZ=Europe/Stockholm

WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 6565
ENTRYPOINT ["java","-jar","app.jar"]
