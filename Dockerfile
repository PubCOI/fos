FROM openjdk:11-jdk-alpine
MAINTAINER info@pubcoi.org
COPY ./target/fos-svc-and-ui.jar /run/fos.jar
