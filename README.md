# StellarStay Hotels System

## Overview
Scalable hotel reservation system, based on hexagonal architecture, with Java microservice and PostgreSQL.

## Architectural Approach
- Hexagonal architecture (ports and adapters)
- Separation of domain, infrastructure, and API
- Dockerization and environment-based configuration

## Tech Stack
- Java 21 + Spring Boot
- PostgreSQL
- Docker, Docker Compose
- JUnit, Mockito

## RFC
The full RFC is in [docs/RFC-001-Architecture.md](docs/RFC-001-Architecture.md)

## Quick Start
```sh
git clone <your-repo>
cd stellarstay-hotels-system
cp .env .env
# For local development
SPRING_PROFILES_ACTIVE=local docker-compose up --build
# For production
SPRING_PROFILES_ACTIVE=prod docker-compose up --build
```

## Spring Profiles & Configuration
- `application.yml`: base configuration
- `application-local.yml`: local development profile
- `application-prod.yml`: production profile

## API Documentation
- GET /api/rooms/available
- POST /api/reservations

## Architecture Summary
- Clear service boundaries
- Hexagonal, decoupled, scalable

## Observability (optional)
- Metrics and structured logs (see RFC)

---
