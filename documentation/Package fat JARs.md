# How to package fat JARs (Spring Boot uber-jars) for deployment 

Run with `java -jar`, instead of running `mvn spring-boot:run` inside each container.



### Step 1 — Build fat JAR locally

From each service directory:

```bash
mvn clean package -DskipTests
```

This creates:

```
target/cart-service-1.0.0.jar
target/order-service-1.0.0.jar
target/catalog-service-1.0.0.jar
```

### Step 2 — Dockerfile for each service

```dockerfile
# ---- Stage 1: Build (skipped if building outside Docker) ----
# FROM eclipse-temurin:21-jdk-alpine AS build
# WORKDIR /app
# COPY . .
# RUN ./mvnw clean package -DskipTests

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app
COPY target/cart-service-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Step 3 — docker-compose.yml

```yaml
services:
  cart-service:
    build: ./ticketing-cart-service
    ports:
      - "8081:8080"

  order-service:
    build: ./ticketing-order-service
    ports:
      - "8082:8080"

  catalog-service:
    build: ./ticketing-catalog-service
    ports:
      - "8083:8080"
```
