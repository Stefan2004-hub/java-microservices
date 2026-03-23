# Java Microservices

A production-style Spring Boot microservices sample demonstrating service discovery, gateway-based routing, synchronous inter-service communication, and per-service PostgreSQL persistence.

## Architecture Overview

This repository contains four independent services:

| Service | Port | Responsibility | Key Technologies |
|---|---:|---|---|
| `discovery-server` | `8761` | Service registry (Eureka Server) | Spring Cloud Netflix Eureka |
| `api-gateway` | `8080` | Single entrypoint and routing | Spring Cloud Gateway (MVC), Eureka Client |
| `product-service` | `8081` | Product catalog CRUD-style APIs | Spring Web, Spring Data JPA, PostgreSQL |
| `order-service` | `8082` | Order placement and product-enriched order details | Spring Web, OpenFeign, Spring Data JPA, PostgreSQL |

### Request Flow

1. Clients call `api-gateway` on port `8080`.
2. Gateway routes:
   - `/products/**` -> `product-service`professional
   - `/orders/**` -> `order-service`
3. `order-service` calls `product-service` through OpenFeign using Eureka service discovery.

## Tech Stack

- Java 17
- Spring Boot 3.5.x
- Spring Cloud 2025.0.x
- Maven Wrapper (`./mvnw`) per service
- PostgreSQL (Docker Compose for local databases)
- JUnit 5 + Spring Boot Test + Testcontainers

## Prerequisites

- JDK 17+
- Docker + Docker Compose
- Git (optional)

## Local Setup

From repository root:

```bash
docker compose up -d
```

This starts:

- `product-db` on host port `5431`
- `order-db` on host port `5432`

## Run Services (Recommended Order)

Open four terminals from repository root and run:

1. Discovery server

```bash
cd discovery-server
./mvnw spring-boot:run
```

2. Product service

```bash
cd product-service
./mvnw spring-boot:run
```

3. Order service

```bash
cd order-service
./mvnw spring-boot:run
```

4. API gateway

```bash
cd api-gateway
./mvnw spring-boot:run
```

## Service Endpoints

### Through API Gateway (`http://localhost:8080`)

- `POST /products`
- `GET /products`
- `GET /products/{id}`
- `POST /orders`
- `GET /orders/{id}/details`

### Direct (Bypassing Gateway)

- Product service: `http://localhost:8081/products`
- Order service: `http://localhost:8082/orders`
- Eureka dashboard: `http://localhost:8761`

## API Examples

### Create Product

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "price": 1200.0
  }'
```

Example response (`201 Created`):

```json
{
  "id": 1,
  "name": "Laptop",
  "price": 1200.0
}
```

### List Products

```bash
curl http://localhost:8080/products
```

### Place Order

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

Example response (`201 Created`):

```json
{
  "id": 1,
  "productId": 1,
  "quantity": 2,
  "totalPrice": 2400.0
}
```

### Get Order Details (Aggregated)

```bash
curl http://localhost:8080/orders/1/details
```

Example response (`200 OK`):

```json
{
  "orderId": 1,
  "quantity": 2,
  "totalPrice": 2400.0,
  "product": {
    "id": 1,
    "name": "Laptop",
    "price": 1200.0
  }
}
```

## Validation and Error Handling

- `product-service` validates incoming product payloads (`name`, `price`) and returns `400 Bad Request` for invalid fields.
- Both `product-service` and `order-service` return `404 Not Found` with structured error payloads for missing resources.
- `order-service` maps product lookup failures to a not-found business error when placing orders.

## Build and Test

Compile each service:

```bash
cd discovery-server && ./mvnw -DskipTests compile
cd ../api-gateway && ./mvnw -DskipTests compile
cd ../product-service && ./mvnw -DskipTests compile
cd ../order-service && ./mvnw -DskipTests compile
```

Run tests per service:

```bash
cd product-service && ./mvnw test
cd ../order-service && ./mvnw test
cd ../api-gateway && ./mvnw test
cd ../discovery-server && ./mvnw test
```

## Troubleshooting

- Port conflict: ensure `8080`, `8081`, `8082`, `8761`, `5431`, `5432` are free.
- Services not visible in Eureka: start `discovery-server` first, then restart clients.
- Database connection errors: verify Docker containers are running with `docker compose ps`.
- Order creation fails for existing product IDs: confirm `product-service` is up and registered in Eureka.

## Project Metadata

### Badges

`TODO`: Add CI/build/test badges when pipeline URLs are available.

### License

`TODO`: Add license (for example, MIT/Apache-2.0) and include a `LICENSE` file.

### Contributing

`TODO`: Add contribution guidelines (`CONTRIBUTING.md`) and PR conventions.

## Roadmap

- Add centralized configuration service.
- Add distributed tracing/observability.
- Add authentication/authorization at gateway.
- Add resilience patterns (timeouts, retries, circuit breakers).
