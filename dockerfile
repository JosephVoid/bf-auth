#
# Build stage
#
FROM maven:3.8.3-openjdk-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml -DJAR_NAME=build clean install -U
RUN mvn -f /home/app/pom.xml -DJAR_NAME=build clean package
EXPOSE 8080
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=docker","/home/app/target/build.jar"]