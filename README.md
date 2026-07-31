# Fitness App — Spring Cloud Microservices

A fitness platform built with **Spring Boot** and **Spring Cloud**. Authenticated users register via **Keycloak**, log workouts, and receive **AI-generated coaching recommendations** (OpenAI) when activities are created.

---

## Tech stack

| Layer | Technology |
|-------|------------|
| Language | Java 25 |
| Framework | Spring Boot 4.1.0 |
| Cloud | Spring Cloud 2025.1.2 |
| Build | Maven (per service) |
| API docs | SpringDoc OpenAPI |
| Auth | Keycloak (OAuth2 JWT resource server) |
| Messaging | Apache Kafka |
| AI | OpenAI Chat Completions API |

---

## Architecture

Microservices with service discovery, centralized config, and an API gateway. Clients talk only to the gateway; downstream services discover each other via Eureka.

```
Client (e.g. http://localhost:3000)
        │  Bearer JWT (Keycloak realm: fitness-app)
        ▼
┌───────────────────┐
│  API Gateway :8080 │  OAuth2 JWT validation + user sync
└─────────┬─────────┘
          │  lb:// routes + X-USER-ID header
     ┌────┼────────────────┐
     ▼    ▼                ▼
 user-service   activity-service   ai-service
   :8081           :8082             :8083
     │               │                  │
     │               │  Kafka           │
     │               │  activity-events │
     │               └────────►─────────┘
     │                                  │
     ▼                                  ▼
 PostgreSQL                          OpenAI API
                                   MongoDB (shared pattern)
 activity-service & ai-service → MongoDB

 Eureka :8761  ← all services register
 Config Server :8084  ← native classpath config
 Keycloak :8181  ← identity provider
```

### Request flow

1. Client sends a request with a Keycloak JWT to the **gateway** (`:8080`).
2. Gateway validates the JWT (JWK from Keycloak), syncs/creates the user in **user-service**, and forwards with `X-USER-ID`.
3. Routes:
   - `/api/users/**` → user-service
   - `/api/activities/**` → activity-service
   - `/api/ai/**` → ai-service
4. Creating an activity saves it to MongoDB and publishes an event to Kafka topic `activity-events`.
5. **ai-service** consumes the event, calls OpenAI, and stores a structured recommendation in MongoDB.

---

## Services

| Service | Port | Responsibility |
|---------|------|----------------|
| **eureka** | 8761 | Netflix Eureka service discovery |
| **config-server** | 8084 | Centralized config (native profile, `classpath:/config`) |
| **gateway** | 8080 | Spring Cloud Gateway, OAuth2 JWT, CORS, user sync |
| **user-service** | 8081 | User registration and lookup (JPA + PostgreSQL) |
| **activity-service** | 8082 | Workout CRUD + Kafka producer |
| **ai-service** | 8083 | Kafka consumer, OpenAI recommendations, recommendation APIs |

### API surface (via gateway)

**Users** — `/api/users`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/users` | Register user |
| `GET` | `/api/users/{email}` | Get user by email |

**Activities** — `/api/activities`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/activities` | Create activity (publishes Kafka event) |
| `GET` | `/api/activities/{userId}` | List activities for a user |
| `PUT` | `/api/activities/{activityId}` | Update activity |
| `DELETE` | `/api/activities/{activityId}` | Delete activity |

Supported activity types: `RUNNING`, `YOGA`, `SWIMMING`, `CYCLING`, `WEIGHT_TRAINING`, `CARDIO`, `STRETCHING`, `OTHER`.

**AI** — `/api/ai`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/ai/{userId}` | Get recommendations for a user |
| `POST` | `/api/ai/{activityId}` | Regenerate recommendation for an activity |

---

## Databases

| Database | Used by | Purpose |
|----------|---------|---------|
| **PostgreSQL** | user-service | Relational user store (Hibernate `ddl-auto: update`) |
| **MongoDB** | activity-service, ai-service | Activities and AI recommendations |

Connection details are supplied via environment variables (see below). Runtime config lives in:

`config-server/src/main/resources/config/`

---

## Third-party & infrastructure

| Component | Role | Default local endpoint |
|-----------|------|------------------------|
| **Keycloak** 26.6.4 | Identity provider; realm `fitness-app`; JWT issuer | `http://localhost:8181` |
| **OpenAI** | Generates coaching recommendations from activity events | Configured via env / `.env` |
| **Apache Kafka** 4.0.0 | Async pipeline: activity → AI | `localhost:9092` |
| **PostgreSQL** 17 | User database | `localhost:5432` |
| **MongoDB** | Activities & recommendations | Via `MONGO_DB_URL` |
| **Eureka** | Service discovery | `http://localhost:8761` |
| **Config Server** | Shared YAML for gateway & business services | `http://localhost:8084` |

A frontend is expected at `http://localhost:3000` (CORS allowlist on the gateway). That UI is not part of this repository.

---

## Project structure

```
.
├── eureka/              # Service discovery
├── config-server/       # Native config server + config/*.yml
├── gateway/             # API gateway + Keycloak JWT filter
├── user-service/        # Users (PostgreSQL)
├── activity-service/    # Activities (MongoDB) + Kafka producer
└── ai-service/          # Recommendations (MongoDB) + Kafka consumer + OpenAI
```

Each service is a standalone Maven project (`pom.xml` + `./mvnw`). There is no parent aggregator POM.

Centralized runtime config:

```
config-server/src/main/resources/config/
├── gateway-service.yml
├── user-service.yml
├── activity-service.yml
└── ai-service.yml
```

---

## Prerequisites

- JDK **25**
- Maven (or use each service’s `./mvnw`)
- Docker (for PostgreSQL, Kafka, Keycloak, MongoDB)
- OpenAI API key
- Keycloak realm **`fitness-app`** configured for your client

---

## Environment variables

| Variable | Service(s) | Description |
|----------|------------|-------------|
| `DATABASE_URL` | user-service | JDBC URL (e.g. `jdbc:postgresql://localhost:5432/mydb`) |
| `DATABASE_USERNAME` | user-service | Postgres username |
| `DATABASE_PASSWORD` | user-service | Postgres password |
| `MONGO_DB_URL` | activity-service, ai-service | MongoDB connection URI |
| `DATABASE_NAME` | activity-service, ai-service | MongoDB database name |
| `OPEN_AI_URL` | ai-service | OpenAI API base URL |
| `OPEN_AI_KEY` | ai-service | OpenAI API key |
| `OPEN_AI_MODEL` | ai-service | Model name (default: `gpt-5-mini`) |

`ai-service` can load OpenAI settings from a local `.env` file (gitignored). **Do not commit secrets.**

---

## Running locally

### 1. Start infrastructure

```bash
# PostgreSQL
docker run \
  --name postgres-db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_DB=mydb \
  -v postgres-data:/var/lib/postgresql/data \
  -p 5432:5432 \
  -d postgres:17

# MongoDB (example)
docker run --name mongo-db \
  -p 27017:27017 \
  -d mongo:7

# Kafka
docker run -p 9092:9092 apache/kafka:4.0.0

# Keycloak (dev mode)
docker run -p 127.0.0.1:8181:8080 \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:26.6.4 start-dev
```

Configure a Keycloak realm named **`fitness-app`** so the gateway JWK URI resolves:

`http://localhost:8181/realms/fitness-app/protocol/openid-connect/certs`

### 2. Export env vars

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/mydb
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=password
export MONGO_DB_URL=mongodb://localhost:27017
export DATABASE_NAME=fitness
export OPEN_AI_URL=https://api.openai.com/v1/chat/completions
export OPEN_AI_KEY=sk-...
export OPEN_AI_MODEL=gpt-5-mini
```

### 3. Start services (order matters)

```bash
# 1. Discovery
cd eureka && ./mvnw spring-boot:run

# 2. Config
cd config-server && ./mvnw spring-boot:run

# 3. Business services (any order after Eureka + Config)
cd user-service && ./mvnw spring-boot:run
cd activity-service && ./mvnw spring-boot:run
cd ai-service && ./mvnw spring-boot:run

# 4. Gateway
cd gateway && ./mvnw spring-boot:run
```

### 4. Call the API

- Gateway: `http://localhost:8080`
- Eureka dashboard: `http://localhost:8761`
- Include `Authorization: Bearer <keycloak-access-token>` on requests

OpenAPI / Swagger UI is available on the individual business services when hit directly (SpringDoc).

---

## Ports quick reference

| Component | Port |
|-----------|------|
| Gateway | 8080 |
| User service | 8081 |
| Activity service | 8082 |
| AI service | 8083 |
| Config server | 8084 |
| Eureka | 8761 |
| Keycloak | 8181 |
| Kafka | 9092 |
| PostgreSQL | 5432 |
| MongoDB | 27017 |

---

## License

Add a license of your choice before publishing if needed.
