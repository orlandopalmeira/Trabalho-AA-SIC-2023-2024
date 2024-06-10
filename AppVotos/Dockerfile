FROM ubuntu:22.04

COPY ./app/votingapp /app

RUN apt-get update && apt-get install -y openjdk-21-jdk && apt-get install -y maven

WORKDIR /app

RUN mvn clean
RUN mvn package

EXPOSE 8080

CMD ["java", "-jar", "target/votingapp-0.0.1-SNAPSHOT.war"]