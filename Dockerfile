# Use OpenJDK base image
FROM openjdk:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the built jar file
COPY ./target/scs-0.0.1-SNAPSHOT.jar /app

# Expose port (adjust based on your `application.properties`)
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java", "-jar", "scs-0.0.1-SNAPSHOT.jar"]