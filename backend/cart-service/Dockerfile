FROM eclipse-temurin:21-jdk-alpine

# Install Maven and redis-cli
RUN apk add --no-cache maven redis

# Set working directory inside container
WORKDIR /app

# No need to copy code here; mount it as a volume for live development
