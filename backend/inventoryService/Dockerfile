FROM openjdk:21-jdk-slim

# Install Maven (if needed for development)
RUN apt-get update && apt-get install -y maven

# Set working directory inside container
WORKDIR /app

# Do not copy code here; mount it as a volume for live development
# CMD ["mvn", "spring-boot:run"] or ["./mvnw", "spring-boot:run"] for local wrapper
