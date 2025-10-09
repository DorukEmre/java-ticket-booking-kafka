A full-stack ticket booking application using React (frontend), Spring Boot and Java (backend), and MySQL (database). 
The system follows a microservices architecture, with services communicating via Kafka queues and REST APIs.
All components run in Docker containers.

## Services

Microservices architecture for a ticket booking system:
  - Frontend: React app
  - Backend services: Catalog, Cart, Order
  - API Gateway: gatewayapi
  - Database: MySQL
  - Messaging: Kafka + Zookeeper + Schema Registry + Kafka UI

# React + Java + MySQL in Docker Boilerplate

Boilerplate to set up a full-stack application using React for the frontend, Java projects for the backend, and MySQL for the database, all within Docker containers. 

## Database

MySQL in Docker container

### Tables

## Frontend

Demo set up to make a get and a post request to catalog backend

## Backend

The backend consists of Java-based microservices (e.g., CatalogService) running in separate Docker containers and orchestrated using Docker Compose. Each service exposes RESTful APIs for the frontend to interact with. The backend services connect to the MySQL database for data persistence and retrieval.

- Each backend service has its own Dockerfile and configuration.
- Services communicate with the database using JDBC or ORM frameworks.
- API endpoints are designed for CRUD operations and business logic.

## Role of each microservice
- gateway-api → entry point (routing, throttling)
- cart-service → temporary state, fast (Redis)
- order-service → orchestrator, creates orders (Order, Customer)
- catalog-service → source of truth for availability and price (Event, Venue)

## Architecture & Data Flow

- Client keeps a local cartId in localStorage with cartId and a local copy of items for instant UI.
- Server exposes idempotent APIs: createCart, getCart(cartId), saveCartItem(cartId, item), deleteCartItem(cartId, item), checkout(cartId).
- On saveCartItem, createCart on server if needed and persist cartId locally.
- Server stores cart in Redis for fast reads.

- Server treated as source of truth: price and availability validated at checkout and when presenting totals.
- Items not reserved on saveCartItem, only at checkout
- Abandoned cart TTL: server expires carts after some time.

## Purchase Flow
Ticket purchase request flow through the system. Each service communicates via Kafka events to ensure reliable and decoupled processing.

1) Frontend → Cart Service (HTTP)
    
   → emit `OrderCreationRequested`

2) Order Service consumes
- Check cart not already processed
- Save Order(`PENDING`)

  → emit `ReserveInventory`

3) Catalog Service consumes
- Checks availability and updates stock

  → emit `InventoryReservationSucceeded` OR `InventoryReservationFailed`

4) Order Service consumes
- If success: update Order(`CONFIRMED`)  
  → emit `OrderCreationSucceeded`
- If fail: update Order(`FAILED`)  
  → emit `OrderCreationFailed`

5) Cart Service consumes
- Update cart/order status

6) Frontend Cart Update
  - Poll `/cart/{cartId}/status` (or subscribe via WebSocket for real-time updates)
  - If status = `CONFIRMED` → redirect to /orders/{orderId}
  - If status = `FAILED` → display checkout failed


## Microservice Structure

- **Controller**: Handles incoming HTTP requests, maps them to service methods, and returns responses. Defines API endpoints.
- **Entity**: Represents a table in the database. Each entity is a Java class annotated for ORM (e.g., JPA/Hibernate).
  - **JPA (Java Persistence API)**: A specification for managing relational data in Java applications. It defines how Java objects are mapped to database tables.
  - **ORM (Object-Relational Mapping)**: A technique that lets you interact with a database using objects instead of SQL queries. Frameworks like Hibernate implement ORM and JPA.
- **Repository**: Provides CRUD operations for entities. Interfaces typically extend JPARepository or similar.
- **Service**: Contains business logic and interacts with repositories. Called by controllers to process requests.
- **Response**: Defines the structure of data sent back to the frontend, often as DTOs (Data Transfer Objects) for API responses.
- **JDBC (Java Database Connectivity)**: A standard Java API for connecting and executing queries with relational databases using SQL. JDBC provides low-level access to the database and is often used directly or by ORM frameworks.

#### Java Project Dependencies

- **spring-boot-starter-web**: For building RESTful web applications.
- **spring-boot-starter-data-jpa**: For ORM and database access.
- **mysql-connector-j**: MySQL database driver.
- **flyway-core** and **flyway-mysql**: For database migrations.
- **lombok**: For reducing boilerplate code in Java classes.
- **spring-boot-starter-test**: For testing support.

## Spring Boot Actuator

- `http://localhost:8000/actuator/gateway/routes` — View all routes currently configured in the API Gateway, including their IDs, predicates, and target URIs.

**Note:**  
Enabled and exposed in the gatewayapi’s `application.properties` using:
```
management.endpoints.web.exposure.include=*
management.endpoint.gateway.access=unrestricted
```

## API Tests (Bruno)
The `api-tests` folder contains Bruno (API client) collections for testing the REST APIs of all backend services.
