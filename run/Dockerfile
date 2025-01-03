# Use a base image for Java
FROM openjdk:21-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the Spring Boot application JAR file into the container
COPY /football-management-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application will run on (e.g., 8080)
EXPOSE 8080

# Command to run the JAR file
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]