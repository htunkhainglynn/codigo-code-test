# Use OpenJDK image
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the Spring Boot JAR
COPY target/*.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
