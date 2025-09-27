# StellarStay Hotels System

## Project Overview
StellarStay Hotels System is a scalable platform for hotel reservation management, designed to handle over 50,000 bookings per day, with dynamic pricing, external integrations (payments, notifications), and high availability. The system follows a hexagonal architecture based on microservices, ensuring separation of concerns, maintainability, and scalability.

### Architectural Approach
- Hexagonal architecture (Ports & Adapters) with independent microservices.
- Clear separation between domain, ports, and adapters.
- Synchronous (REST) and asynchronous (event-driven) communication.
- Reliability patterns: circuit breaker, retry, bulkhead.

### Technology Stack
- Java, Spring Boot, Spring Data JPA, Resilience4j, Spring Cloud Sleuth
- PostgreSQL, Redis, MongoDB (optional)
- Apache Kafka, RabbitMQ (alternative)
- Docker, Kubernetes
- Prometheus, Grafana

---

## RFC Document
The complete architecture document is available at [docs/RFC-001-Architecture.md](docs/RFC-001-Architecture.md).

---

## Quick Start

```bash
# Clone the repository
$ git clone <your-repo>
$ cd stellarstay-hotels-system

# Build the project
$ ./mvnw clean install

# Run tests
$ ./mvnw test
```

---

## How to Run the API

### Local development
You can start the API for development using:

```bash
docker compose -f docker-compose.dev.yml --env-file .env.dev up -d
```

### Production
To start the API for production, use:

```bash
docker compose -f docker-compose.yml --env-file .env.prod up -d
```

> **Note:** A `.env.example` file is provided as a template. For security reasons, both `.env.dev` and `.env.prod` files must be requested from the API administrator (actually the developer) and are not included in the repository.

---

## API Documentation

### 1. GET /api/rooms/available
- **Description:** Query available rooms based on search criteria.
- **Sample request:**
  ```bash
  curl --location 'http://localhost:8080/api/rooms/available?checkInDate=2025-10-03&checkOutDate=2025-10-04&guests=2'
  ```
- **Successful response:**
  ```json
  [
      {
          "roomId": 1,
          "type": "JUNIOR_SUITE",
          "capacity": 2,
          "available": true
      },
      {
          "roomId": 2,
          "type": "KING_SUITE",
          "capacity": 3,
          "available": true
      },
      {
          "roomId": 3,
          "type": "PRESIDENTIAL_SUITE",
          "capacity": 4,
          "available": true
      }
  ]
  ```
- **Errors:**
  - 400: Invalid parameters
  - 500: Internal server error

### 2. POST /api/reservations
- **Description:** Create a new reservation applying pricing rules and validating availability.
- **Sample request:**
  ```bash
  curl --location 'http://localhost:8080/api/reservations' \
  --header 'Content-Type: application/json' \
  --data '{
      "roomId": 1,
      "guestName": "Diego Salazar",
      "guests": 1,
      "checkInDate": "2025-10-07",
      "checkOutDate": "2025-10-08",
      "breakfastIncluded": true
    }'
  ```
- **Successful response:**
  ```json
  {
      "reservationId": 2,
      "roomId": 1,
      "roomType": "JUNIOR_SUITE",
      "guestName": "Diego Salazar",
      "guests": 1,
      "checkInDate": "2025-10-07",
      "checkOutDate": "2025-10-08",
      "breakfastIncluded": true,
      "totalPrice": 65.0
  }
  ```
- **Errors:**
  - 400: Invalid data or room not available
  - 409: Reservation conflict
  - 500: Internal server error

---

## Data Integrity and Concurrency Control

To guarantee data integrity and avoid race conditions in the reservation process, the following strategy is applied:

- The `@Transactional(isolation = Isolation.READ_COMMITTED)` annotation ensures that each reservation operation only sees committed data, reducing unnecessary locking and improving performance compared to stricter isolation levels.
- The query for overlapping reservations uses a pessimistic lock (`@Lock(LockModeType.PESSIMISTIC_WRITE)`), which locks the relevant reservation rows for the selected room and date range during the transaction.
- This means that if two users try to reserve the same room for overlapping dates at the same time, only one transaction will proceed; the other will wait and, upon retrying, will see that the room is no longer available.
- The lock is held on the reservation records until the transaction completes (commit or rollback), ensuring that no overbooking or double booking can occur.

This approach is very effective for guaranteeing integrity in critical operations such as hotel reservations, inventory management, or financial transfers, where concurrent modifications must be strictly controlled.

---

## Scalability and Bulkhead Pattern

To ensure the system can handle high concurrency and remain stable under heavy load, the Bulkhead pattern has been applied to the reservation creation process:

- The `createReservation` method in `ReservationUseCaseImpl` is annotated with `@Bulkhead` from the Resilience4j library.
- Bulkhead configuration is defined in `application.yml`, setting strict limits on concurrent executions and queued requests for reservation creation.
- This approach does not modify business logic or repository code, but transparently protects the system from overload in this critical section.
- The API can now handle up to **50 concurrent reservation requests** and up to **200 additional requests in queue**. This is aligned with the business goal of supporting thousands of daily reservations and high-traffic peaks.
- If the system is overloaded, new requests will wait in the queue or be rejected, but the rest of the API remains responsive and stable.

This configuration helps protect the overall system stability and maintain high availability (99.9% uptime) during peak periods, as required by the business context.

---

## Project Structure (Package Distribution)

The following diagram shows the actual package and class distribution for the StellarStay Hotels System, reflecting both `adapters/in` and `adapters/out` as in your project:

```
stellarstay-hotels-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── stellarstay/
│   │   │           └── hotelsystem/
│   │   │               ├── StellarstayHotelsSystemApplication.java
│   │   │               ├── adapters/
│   │   │               │   ├── in/
│   │   │               │   │   ├── ReservationUseCaseImpl.java
│   │   │               │   │   └── RoomAvailabilityUseCaseImpl.java
│   │   │               │   └── out/
│   │   │               │       └── (outbound adapters here)
│   │   │               ├── api/
│   │   │               │   ├── ReservationController.java
│   │   │               │   ├── RoomController.java
│   │   │               │   ├── dto/
│   │   │               │   ├── exception/
│   │   │               │   └── validation/
│   │   │               ├── config/
│   │   │               │   ├── CorrelationIdFilter.java
│   │   │               │   ├── DomainConfig.java
│   │   │               │   ├── KafkaConfig.java
│   │   │               │   └── Resilience4jConfig.java
│   │   │               ├── domain/
│   │   │               │   ├── PriceCalculator.java
│   │   │               │   ├── Reservation.java
│   │   │               │   ├── Room.java
│   │   │               │   └── RoomType.java
│   │   │               └── ports/
│   │   │                   └── in/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── data.sql
│   │       └── schema.sql
│   └── test/
│       ├── java/
│       │   └── (tests here with same distribution)
│       └── resources/
│           └── application-test.yml
├── docker-compose.yml
├── docker-compose.dev.yml
├── Dockerfile
├── pom.xml
├── README.md
└── docs/
    └── RFC-001-Architecture.md
```

This structure follows the hexagonal architecture, clearly separating domain logic, inbound and outbound adapters, ports, configuration, and API layers for maintainability and scalability.

---

## Architecture Summary
- Hexagonal architecture with clear separation of domain, ports, and adapters.
- Decoupled and scalable microservices.
- Reliability patterns implemented as per the RFC.
- Business logic and pricing rules meet business requirements.
- Integrated observability and monitoring.

### Advantages
- Horizontal scalability and resilience.
- Easy maintenance and evolution.
- Simple integration of new services or adapters.

### Drawbacks and Future Improvements
- Initial deployment and monitoring complexity.
- Possible latency in external integrations.
- Improvements: CI/CD automation, stress testing, cache and event optimization.

---

## Observability and Reliability
- **Logging:** All services implement structured logging with Correlation ID for end-to-end traceability across requests and services.
- **Metrics:** Prometheus is integrated for collecting and exposing technical and business metrics (API latency, reservation success rate, error types, etc.).
- **Reliability Patterns:**
  - **Retry and Circuit Breaker:** All communication with external services (e.g., Kafka, payment gateways) uses retry and circuit breaker patterns to ensure resilience and fault tolerance. This prevents cascading failures and improves system stability under transient errors or outages.

---

## Troubleshooting
- Check service logs with `docker-compose logs`.
- Verify health endpoints (`/actuator/health`).
- Consult metrics and alerts in Grafana.

---

## License
MIT

---

## Next Steps and Recommendations

### API Documentation with Swagger/OpenAPI
Integrating Swagger (OpenAPI) is highly recommended for the next iteration. Swagger provides interactive, always up-to-date API documentation, making it easier for developers and integrators to understand, test, and consume the API. It also helps ensure contract clarity and accelerates onboarding for new team members or external partners.

### Outbox Pattern for Reservation Events (Kafka)
For the POST /api/reservations endpoint, implementing the Outbox pattern is a best practice to ensure reliable event-driven communication. This involves:
- Creating an `outbox` table in the database to store reservation events as part of the same transaction that creates the reservation.
- A background process or service reads from the outbox table and publishes events to Kafka.
- This approach decouples reservation creation from asynchronous event delivery, guarantees no event loss, and avoids inconsistencies between the database and Kafka (solving the "dual write" problem).
- External services can reliably consume reservation creation events from Kafka, enabling integrations such as notifications, analytics, or downstream processing.

### Grafana Integration
While Prometheus is already integrated for metrics collection, adding Grafana is recommended for advanced visualization and alerting. Grafana provides:
- Custom dashboards for real-time monitoring of business and technical KPIs (e.g., reservation rates, error rates, latency).
- Alerting capabilities to notify the team of anomalies or outages.
- A user-friendly interface for both developers and business stakeholders to track system health and performance.

### Additional Recommendations
- Expand automated testing (unit, integration, and end-to-end) for all critical flows.
- Implement CI/CD pipelines for automated build, test, and deployment.
- Consider security best practices: API authentication, authorization, and secrets management.
- Document error codes and business rules in the API documentation.

By following these next steps, the StellarStay Hotels System will further improve its reliability, maintainability, and developer experience, while enabling robust integrations with external systems.
