# Use official Java 17 image
FROM openjdk:17.0.2-jdk

# Set working directory
WORKDIR /app

COPY target/sprih-assignment-0.0.1-SNAPSHOT.jar app.jar
# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]