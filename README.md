A full-stack ticket booking application using React (frontend), Spring Boot and Java (backend), and MySQL (database). 
The system follows a microservices architecture, with services communicating via Kafka queues and REST APIs.
All components run in Docker containers.

## Services

- API Gateway
- Inventory
- Booking
- Order

## Database

Venues can host Events

# React + Java + MySQL in Docker Boilerplate

Boilerplate to set up a full-stack application using React for the frontend, Java projects for the backend, and MySQL for the database, all within Docker containers. 

## Database

MySQL in Docker container

## Frontend

Demo set up to make a get and a post request to inventory backend

## Backend

The backend consists of Java-based microservices (e.g., Inventory) running in separate Docker containers. Each service exposes RESTful APIs for the frontend to interact with. The backend services connect to the MySQL database for data persistence and retrieval.

- Each backend service has its own Dockerfile and configuration.
- Services communicate with the database using JDBC or ORM frameworks.
- API endpoints are designed for CRUD operations and business logic.

### Interactions

- The frontend (React) sends HTTP requests to backend endpoints.
- Backend services process requests, interact with the MySQL database, and return responses to the frontend.
- All services are orchestrated using Docker Compose.

### Structure

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


## Services

- Inventory
