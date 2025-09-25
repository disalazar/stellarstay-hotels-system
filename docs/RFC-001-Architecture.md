# RFC-001: StellarStay Hotels System Architecture

## 1. Executive Summary and Requirements
(To be completed according to the prompt and your analysis)

## 2. System Architecture
- Services, ports, adapters
- Communication diagrams

## 3. Scalability and Reliability Strategy
- Horizontal/vertical scalability
- Reliability patterns

## 4. Data Architecture
- Database decisions
- Consistency and performance

## 5. Tech Stack Justification
- Java, Spring Boot, PostgreSQL, etc.

---
(Complete each section according to the prompt and your design)
# Dockerfile for StellarStay Hotels System
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml pom.xml
RUN ./mvnw dependency:go-offline

COPY src src

RUN ./mvnw package -DskipTests

EXPOSE 8080

ENV JAVA_OPTS=""
CMD ["sh", "-c", "java $JAVA_OPTS -jar target/*.jar"]
