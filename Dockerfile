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

ARG ARG_DO_S3_KEY
ARG ARG_DO_S3_SECRET
ARG ARG_DO_S3_ENDPOINT
ARG ARG_DO_S3_REGION
ARG ARG_DO_S3_BUCKET

ENV DO_S3_KEY=$ARG_DO_S3_KEY
ENV DO_S3_SECRET=$ARG_DO_S3_SECRET
ENV DO_S3_ENDPOINT=$ARG_DO_S3_ENDPOINT
ENV DO_S3_REGION=$ARG_DO_S3_REGION
ENV DO_S3_BUCKET=$DO_S3_BUCKET

ENTRYPOINT java -jar $JAVA_OPTIONS /run/HRM_BE.jar
