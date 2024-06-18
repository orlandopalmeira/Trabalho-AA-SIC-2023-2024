# Stage 1: Build the application
FROM maven:3.9.7 AS build
WORKDIR /app
COPY ./app/votingapp /app
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM openjdk:21-jdk
WORKDIR /app
COPY --from=build /app/target/votingapp-0.0.1-SNAPSHOT.war /app/votingapp.war
EXPOSE 8080
CMD ["java", "-jar", "/app/votingapp.war"]
