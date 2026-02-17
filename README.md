# Time Deposit API

RESTful API for managing time deposit accounts with automatic monthly interest calculation. Built with Spring Boot 3, following **Hexagonal Architecture** and **Strategy Pattern** for extensible interest logic.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Key Design Decisions & Changes](#key-design-decisions--changes)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Swagger / OpenAPI](#swagger--openapi)
- [Interest Calculation Rules](#interest-calculation-rules)
- [Database Schema](#database-schema)
- [Sample Data](#sample-data)
- [Testing](#testing)
- [H2 Console](#h2-console)

---

## Overview

This API provides exactly **two** endpoints:

1. **GET /api/time-deposits** — Retrieve all time deposits with balances and withdrawal history.
2. **PUT /api/time-deposits/update-balances** — Calculate and apply monthly interest to all time deposits based on plan type and duration.

Interest is computed via pluggable **strategies** (Basic, Student, Premium), so new plan types can be added without changing existing code.

---

## Tech Stack

| Category        | Technology                          |
|----------------|-------------------------------------|
| Runtime        | Java 17                             |
| Framework      | Spring Boot 3.2                     |
| Build          | Maven                               |
| Persistence    | Spring Data JPA, H2 (dev), PostgreSQL (tests) |
| API Docs       | SpringDoc OpenAPI 3 (Swagger UI)    |
| Testing        | JUnit 5, MockMvc, **Testcontainers** (PostgreSQL) |
| Utilities      | Lombok                              |

---

## Prerequisites

- **Java 17+**
- **Maven 3.6+**
- **Docker** (required for integration tests using Testcontainers)

---

## Project Structure

```
time-deposit-api/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/org/ikigaidigital/
│   │   │   ├── TimeDepositApplication.java
│   │   │   ├── config/
│   │   │   │   └── OpenApiConfig.java              # OpenAPI / Swagger config
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── TimeDeposit.java            # Domain entity
│   │   │   │   │   └── Withdrawal.java
│   │   │   │   ├── port/
│   │   │   │   │   ├── in/
│   │   │   │   │   │   ├── GetAllTimeDepositsUseCase.java
│   │   │   │   │   │   └── UpdateBalancesUseCase.java
│   │   │   │   │   └── out/
│   │   │   │   │       └── TimeDepositRepository.java
│   │   │   │   └── service/
│   │   │   │       ├── TimeDepositCalculator.java # Uses strategy factory
│   │   │   │       ├── TimeDepositService.java    # Implements use cases
│   │   │   │       └── strategy/                   # Interest strategies
│   │   │   │           ├── InterestStrategy.java
│   │   │   │           ├── InterestStrategyFactory.java
│   │   │   │           ├── BasicInterestStrategy.java
│   │   │   │           ├── StudentInterestStrategy.java
│   │   │   │           └── PremiumInterestStrategy.java
│   │   │   └── adapter/
│   │   │       ├── in/web/
│   │   │       │   ├── TimeDepositController.java
│   │   │       │   └── dto/
│   │   │       │       ├── TimeDepositResponse.java
│   │   │       │       └── WithdrawalResponse.java
│   │   │       └── out/persistence/
│   │   │           ├── TimeDepositJpaRepository.java
│   │   │           ├── TimeDepositPersistenceAdapter.java
│   │   │           └── entity/
│   │   │               ├── TimeDepositEntity.java
│   │   │               └── WithdrawalEntity.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── data.sql                            # Seed data
│   └── test/
│       └── java/org/ikigaidigital/
│           ├── TimeDepositIntegrationTest.java    # MockMvc + Testcontainers
│           ├── TimeDepositCalculatorTest.java     # Calculator + strategies
│           └── domain/
│               └── service/
│                   ├── TimeDepositServiceTest.java
│                   └── strategy/
│                       ├── InterestStrategyTest.java
│                       └── InterestStrategyFactoryTest.java
```

### Layer Summary

| Layer            | Location                    | Responsibility |
|------------------|-----------------------------|----------------|
| **Domain**       | `domain/model`, `domain/port`, `domain/service` | Entities, use-case interfaces, repository port, calculator + strategies |
| **Inbound**      | `adapter/in/web`            | REST controller, DTOs, HTTP → use cases |
| **Outbound**     | `adapter/out/persistence`   | JPA entities, repository implementation, DB access |
| **Config**       | `config`                    | OpenAPI bean |

---

## Key Design Decisions & Changes

### 1. Hexagonal Architecture

- **Ports (in):** `GetAllTimeDepositsUseCase`, `UpdateBalancesUseCase` — define what the application does.
- **Port (out):** `TimeDepositRepository` — defines persistence.
- **Adapters:** Controller implements HTTP; `TimeDepositPersistenceAdapter` implements repository with JPA.
- Domain has no dependency on Spring or JPA; only on ports and domain models.

### 2. Strategy Pattern for Interest

- **`InterestStrategy`** interface: `calculateInterest(TimeDeposit)`, `getPlanType()`.
- **Concrete strategies:** `BasicInterestStrategy`, `StudentInterestStrategy`, `PremiumInterestStrategy`.
- **`InterestStrategyFactory`** injects all strategies and resolves by `planType`.
- **`TimeDepositCalculator.updateBalance(List<TimeDeposit>)`** signature is unchanged; it delegates per deposit to the appropriate strategy and adds rounded interest (2 decimals, HALF_UP).

This keeps the original `updateBalance` behaviour intact and makes it easy to add new plan types without modifying existing code (Open/Closed Principle).

### 3. OpenAPI / Swagger

- **SpringDoc** provides Swagger UI and machine-readable API docs.
- **OpenApiConfig** sets API title, description, version, contact.
- DTOs and controller methods are annotated with `@Schema`, `@Operation`, `@ApiResponse` for clear contract and try-it-out in Swagger UI.

### 4. Testcontainers for Integration Tests

- **`TimeDepositIntegrationTest`** uses a **PostgreSQL** container (`postgres:15-alpine`) via Testcontainers.
- `@DynamicPropertySource` overrides datasource URL, credentials, driver, and JPA dialect so the app runs against the container.
- JPA creates schema; `data.sql` seeds data (`spring.sql.init.mode: always`, `defer-datasource-initialization: true`).
- Tests are **order-independent**: assertions use JSONPath filters by `id` (e.g. `$[?(@.id == 1)].balance`) instead of array indices.
- **`@Transactional`** on the test class rolls back after each test so all tests see the same initial data.

### 5. No Breaking Changes

- Shared **`TimeDeposit`** class is unchanged (same fields and constructors).
- **`TimeDepositCalculator.updateBalance(List<TimeDeposit> xs)`** signature and high-level behaviour (fetch → calculate interest → add rounded interest → set balance) are preserved.

---

## Running the Application

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

The app starts at **http://localhost:8080**.

---

## API Endpoints

### 1. Get all time deposits

```http
GET /api/time-deposits
```

**Response:** `200 OK` — JSON array of time deposits.

**Schema (each element):**

| Field        | Type     | Description                    |
|-------------|----------|--------------------------------|
| id          | integer  | Primary key                    |
| planType    | string   | `basic`, `student`, or `premium` |
| balance     | number   | Current balance                |
| days        | integer  | Days the deposit has been active |
| withdrawals| array    | List of withdrawals            |

**Withdrawal object:** `id`, `timeDepositId`, `amount`, `date`.

**Example response (trimmed):**

```json
[
  {
    "id": 1,
    "planType": "basic",
    "balance": 10000.00,
    "days": 45,
    "withdrawals": [
      {
        "id": 1,
        "timeDepositId": 1,
        "amount": 500.00,
        "date": "2024-01-15"
      }
    ]
  }
]
```

### 2. Update all balances

```http
PUT /api/time-deposits/update-balances
```

Applies one “monthly” interest run to all time deposits (per plan rules). No request body.

**Response:** `200 OK` (no body).

---

## Swagger / OpenAPI

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

### How to trigger the endpoints via Swagger

1. Start the app: `mvn spring-boot:run`
2. Open http://localhost:8080/swagger-ui.html
3. Under **Time Deposits** you will see:
    - **GET /api/time-deposits**
    - **PUT /api/time-deposits/update-balances**

**Suggested flow:**

1. Call **GET /api/time-deposits** → see initial balances and withdrawals.
2. Call **PUT /api/time-deposits/update-balances** → 200 OK.
3. Call **GET /api/time-deposits** again → balances updated with interest (where rules apply).

---

## Interest Calculation Rules

- **First 30 days:** No interest for any plan.
- **Basic:** After 30 days → **1%** annual rate → monthly = `balance × 0.01 / 12`, rounded to 2 decimals (HALF_UP).
- **Student:** After 30 days and **up to 365 days** → **3%** annual → monthly = `balance × 0.03 / 12`. No interest after 365 days.
- **Premium:** After **45 days** → **5%** annual → monthly = `balance × 0.05 / 12`.

Interest is added once per `PUT /api/time-deposits/update-balances` call.

---

## Database Schema

### Table: `time_deposits`

| Column     | Type        | Constraints   |
|-----------|-------------|---------------|
| id        | Integer     | Primary Key   |
| plan_type | String      | Not Null      |
| days      | Integer     | Not Null      |
| balance   | Decimal(19,2) | Not Null    |

### Table: `withdrawals`

| Column          | Type        | Constraints        |
|-----------------|-------------|--------------------|
| id              | Integer     | Primary Key        |
| time_deposit_id | Integer     | Foreign Key, Not Null |
| amount          | Decimal     | Not Null           |
| date            | Date        | Not Null           |

---

## Sample Data

Seeded by `src/main/resources/data.sql`:

| ID | planType | days | balance   |
|----|----------|------|-----------|
| 1  | basic    | 45   | 10000.00  |
| 2  | student  | 90   | 5000.00   |
| 3  | premium  | 60   | 20000.00  |
| 4  | basic    | 25   | 3000.00   |
| 5  | student  | 400  | 8000.00   |
| 6  | premium  | 120  | 15000.00  |

Withdrawals are linked to time deposits (e.g. deposit 1 has one withdrawal of 500.00 on 2024-01-15). After one balance update, examples: id 1 ≈ 10008.33, id 2 ≈ 5012.50, id 4 stays 3000.00 (≤30 days), id 5 stays 8000.00 (student >365 days).

---

## Testing

```bash
mvn test
```

**Requirements:** Docker must be running for integration tests (Testcontainers).

### Test breakdown

| Test Class                     | Type     | Scope |
|--------------------------------|----------|--------|
| `TimeDepositIntegrationTest`   | Integration | Full stack: MockMvc, Spring context, **PostgreSQL Testcontainer**, `data.sql`; GET/PUT and balance assertions by `id`. |
| `TimeDepositCalculatorTest`    | Unit     | Calculator + strategy factory; various plan types and day thresholds. |
| `TimeDepositServiceTest`       | Unit     | Service with mocked repository and calculator. |
| `InterestStrategyTest`         | Unit     | Each strategy’s `calculateInterest` and `getPlanType`. |
| `InterestStrategyFactoryTest`  | Unit     | Factory returns correct strategy by plan type; throws for unknown. |

Integration tests use **`@Transactional`** so each test method rolls back and the database is in a consistent state for the next test.

---

## H2 Console

When running with the default H2 in-memory DB:

- **URL:** http://localhost:8080/h2-console
- **JDBC URL:** `jdbc:h2:mem:timedeposit`
- **Username:** `sa`
- **Password:** (leave empty)

---

## Summary

- **Two REST endpoints:** GET all time deposits, PUT update all balances.
- **Hexagonal layout:** domain, ports, adapters (web + persistence).
- **Strategy-based interest:** Basic, Student, Premium; extensible without changing `TimeDeposit` or `updateBalance` signature.
- **OpenAPI/Swagger** for contract and try-it-out.
- **Testcontainers** with PostgreSQL for realistic integration tests; unit tests for calculator, service, and strategies.
