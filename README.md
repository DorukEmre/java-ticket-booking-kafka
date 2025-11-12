A full-stack ticket booking application using React + TypeScript (frontend), Spring Boot and Java (backend), and MySQL (database). 

The system uses a microservices architecture, where each service runs in its own **Docker container**.

Frontend and backend communicate via REST APIs through a **Gateway API**.
Microservices communicate via **Kafka events** and internal **REST calls**.

A **Caddy web server** serves a static React application and reverse proxies API requests to the Gateway API service.

**Cloud deployment** to **AWS EC2**.

**CI/CD** with **GitHub Actions**. Pushes to `main` trigger the deployment workflow [.github/workflows/deploy.yml](.github/workflows/deploy.yml), which uses OIDC to assume an AWS role and runs remote commands on EC2 via AWS SSM to update the repo and trigger local build/deploy steps (see `Makefile`). See `documentation/CI-CD.md` for operational details and required secrets.


## Application Architecture

![Architecture diagram](architecture.jpg)

#### Key components:

- **Frontend**: Built with **React** + **TypeScript**
- **Backend**: Microservices powered by **Spring Boot** and **Java**
- **Database**: **MySQL** for persistent data storage

#### Microservices breakdown:

* **gatewayapi:** Routes frontend and external API traffic to appropriate services
* **catalog-service:** Manages the catalog of events and venues stored in **MySQL**
* **cart-service:** Handles user carts using **Redis** for in-memory caching
* **order-service:** Processes orders and payments, interfacing with **MySQL**

#### Infrastructure services:

* **MySQL:** Data persistence
* **Redis:** Acts as an in-memory cache for cart data
* **Kafka + Zookeeper + Schema Registry:** Event-driven communication backbone
* **Caddy:** Serves frontend + reverse-proxies backend

All services are connected via a shared Docker network. Deployed using Docker Compose.


## Backend Directory Structure

Multi-module Maven project using a library of shared DTOs

```
backend/
├── pom.xml                      # Parent POM (packaging=pom)
├── ticketing-common-library/    # Shared DTOs
│       └── pom.xml
└── gatewayapi/                  # API gateway for frontend & routing
│       └── pom.xml
├── catalog-service/             # Event catalog microservice
│       └── pom.xml
├── cart-service/                # Shopping cart microservice
│       └── pom.xml
└── order-service/               # Order and payment microservice
        └── pom.xml
```


## Architecture & Data Flow

- Client keeps a local cartId in localStorage with cartId and a local copy of items for instant UI.
- Server exposes APIs: createCart, getCart(cartId), saveCartItem(cartId, item), deleteCartItem(cartId, item), checkout(cartId).
- On saveCartItem, createCart on server if needed and persist cartId locally.
- Server stores cart in Redis for fast reads.

- Server treated as source of truth: price and availability validated at checkout and when presenting totals.
- Items not reserved on saveCartItem, only at checkout
- Abandoned cart TTL: server expires carts after some time.


## Purchase Flow
Ticket purchase request flow through the system. Each service communicates via Kafka events to ensure reliable and decoupled processing.

1) Frontend (`/cart`) → Cart Service (HTTP - `POST cart checkout`)
    
   → emit `OrderCreationRequested`

2) Order Service consumes
- Check cart not already processed
- Save Order(`VALIDATING`)

  → emit `ReserveInventory`

3) Catalog Service consumes
- Checks availability and updates stock

  → emit `InventoryReservationResponse`

4) Order Service consumes
- If success: update Order(`PENDING_PAYMENT`)  
- If invalid: update Order(`INVALID`) 
- If fail: update Order(`FAILED`)  
  → emit `OrderCreationResponse`

5) Cart Service consumes
- Update cart/order status

6) Frontend Cart Update
  - Poll `/cart/{cartId}`
  - If status = `PENDING_PAYMENT` → redirect to `/checkout/{orderId}` for payment
  - If status = `INVALID` → redirect to `/cart` and point out invalid items

See [FRONTEND.md](documentation/FRONTEND.md) for full frontend details.


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
- **lombok**: Generates boilerplate via annotations.
- **spring-kafka**: Spring Kafka integration for event-driven messaging.
- **spring-boot-starter-data-redis**: Redis support.
- **jakarta.validation-api** and **hibernate-validator**: Bean Validation API and implementation.

## Spring Boot Actuator (in dev mode)

- `http://localhost:8000/actuator/gateway/routes` — View all routes currently configured in the API Gateway, including their IDs, predicates, and target URIs.

**Note:**  
Enabled and exposed in the gatewayapi’s `application.properties` using:
```
management.endpoints.web.exposure.include=*
management.endpoint.gateway.access=unrestricted
```

## API Tests (Bruno)
The `api-tests` folder contains Bruno (API client) collections for testing the REST APIs of all backend services.


## Hosting on AWS EC2

- add frontend url to CORS_ALLOWED_ORIGINS
- build frontend with backend api url as VITE_API_BASE_URL
- ensure security group of EC2 instance allows HTTP (port 80) and/or HTTPS (port 443) traffic
- Caddyfile configuration: replace `localhost` with the public DNS or public IP of the EC2 instance

### Inbound rules

Anyone can reach EC2 via ports 80 and 443.
Restrict SSH to my IP + key-based auth.
ICMP for ping/traceroute for diagnostics.

| Type  | Protocol | Port | Source    | Purpose                         |
| ----- | -------- | ---- | --------- | ------------------------------- |
| HTTP  | TCP      | 80   | 0.0.0.0/0 | Public HTTP (redirect to HTTPS) |
| HTTPS | TCP      | 443  | 0.0.0.0/0 | Public HTTPS (production)       |
| SSH   | TCP      | 22   | my.ip/32  | Admin access only               |
| ICMP  | All      | -    | my.ip/32  | Optional diagnostics            |

## CI / CD

- CI/CD is implemented with GitHub Actions. 
- Pushes to `main` trigger the deployment workflow.
- The workflow:
  - Detects frontend/backend changes 
  - Configures AWS credentials via AWS OIDC to assume an AWS role 
  - Runs remote commands on EC2 via AWS SSM to update the repo and trigger local build/deploy steps.
  - Make targets invoked on the instance are defined in [Makefile](Makefile):
    - `update_repo`: Pulls latest changes from origin/main.
    - `deploy_frontend`: Updates repo and rebuilds static frontend.
    - `deploy_backend`: Updates repo, and rebuilds and restarts backend services.

Required GitHub secrets:
- AWS_ACCOUNT_ID
- AWS_REGION
- SSM_INSTANCE_IDS