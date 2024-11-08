##### Dockerfile #####
FROM maven:3.9.9-eclipse-temurin-22-alpine as build
WORKDIR ./src
COPY . .
RUN mvn install -DskipTests=true

FROM eclipse-temurin:17.0.8.1_1-jre-ubi9-minimal

RUN unlink /etc/localtime;ln -s  /usr/share/zoneinfo/Asia/Ho_Chi_Minh /etc/localtime
COPY --from=build src/target/HRM_BE-0.0.1-SNAPSHOT.jar /run/HRM_BE.jar

EXPOSE 8080

ENV JAVA_OPTIONS="-Xmx2048m -Xms256m"
ENTRYPOINT java -jar $JAVA_OPTIONS /run/HRM_BE.jar
