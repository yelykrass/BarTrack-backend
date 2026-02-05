# 🍸 BarTrack Backend

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Security](https://img.shields.io/badge/Security-JWT-orange)
![Database](https://img.shields.io/badge/Database-H2%20%7C%20MySQL-lightgrey)
![Build](https://img.shields.io/badge/Build-Maven-red)
![Status](https://img.shields.io/badge/Status-MVP%20Complete-success)

---

## 📌 Project Overview

**BarTrack Backend** is a robust RESTful API designed to streamline bar inventory management and sales operations. It provides a secure, scalable foundation for tracking items, managing orders, and handling role-based access control.

The current version represents a **Minimum Viable Product (MVP)** with a clean, layered architecture prepared for enterprise-level scaling.

---

## 🏗️ Architecture & Design

### High-Level Architecture

The project follows a classic **3-tier layered architecture** to ensure separation of concerns, high maintainability, and ease of testing.

```mermaid
   flowchart TD
    Client([Client / Frontend]) -- HTTP Request --> Controller

    subgraph Application ["Spring Boot Application"]
        direction TB
        Controller[<b>Controller Layer</b><br/>REST Endpoints & DTOs]
        Service[<b>Service Layer</b><br/>Business Logic & Validation]
        Repository[<b>Repository Layer</b><br/>Data Access / JPA]

        Controller --> Service
        Service --> Repository
    end

    Repository --> DB[(Database<br/>MySQL / H2)]
```

---

### Request Lifecycle & Security Flow

Authentication is handled via JWT stored in HttpOnly Cookies, providing a secure stateless session management.

```mermaid
sequenceDiagram
    autonumber
    participant Client
    participant API as Spring Boot API
    participant DB as Database (MySQL/H2)

    Note over Client, API: Authentication Flow (Login)
    Client->>API: POST /auth/login (LoginRequestDTO)
    activate API
    API->>API: AuthService.login()
    API->>DB: JpaUserDetailsService.loadUserByUsername()
    DB-->>API: UserEntity / SecurityUser
    API->>API: JwtUtils.generateTokens()
    API-->>Client: 200 OK + Cookies (accessToken, refreshToken)
    deactivate API

    Note over Client, API: Secure Request Flow (Create Order)
    Client->>API: POST /api/v1/orders (OrderDTORequest) + Cookies
    activate API
    API->>API: JwtAuthenticationFilter validates accessToken

    alt Token Invalid/Expired
        API-->>Client: 401 Unauthorized
    else Token Valid
        API->>API: SecurityContextHolder setAuthentication()
        API->>API: OrderController.create()
        API->>DB: UserService.getCurrentUser()
        API->>DB: OrderRepository.save(OrderEntity)
        DB-->>API: Saved
        API-->>Client: 201 Created
    end
    deactivate API
```

---

## 🎯 MVP Scope

| Admin Permissions        | User Permissions     | Future (Post-MVP)      |
| ------------------------ | -------------------- | ---------------------- |
| ✅ Full CRUD for items   | ✅ View active items | 🧪 Cocktail recipes    |
| ✅ View all orders       | ✅ Create orders     | 📦 Ingredient tracking |
| ✅ Manage system catalog | ✅ View own orders   | 📊 Sales analytics     |

---

## 🗄️ Database Model

```mermaid
erDiagram
    USERS {
        Long id PK
        String username
        String password
        Boolean active
    }

    ROLES {
        Long id PK
        String name
    }

    ROLES_USERS {
        Long user_id FK
        Long role_id FK
    }

    ITEMS {
        Long id PK
        String name
        String category
        Double price
        Integer quantity
        Boolean active
    }

    ORDERS {
        Long id PK
        Long user_id FK
        Double total_price
        Enum payment_method
        Date created_at
    }

    ORDER_ITEMS {
        Long id PK
        Long order_id FK
        Long item_id FK
        Integer quantity
        Double price_per_unit
    }
USERS ||--o{ ORDERS : creates
    ORDERS ||--|{ ORDER_ITEMS : contains
    ITEMS ||--o{ ORDER_ITEMS : sold_in
    USERS ||--o{ ROLES_USERS : has
    ROLES ||--o{ ROLES_USERS : assigned

```

---

## 🛠️ Technology Stack

### Core

- Java 21 & Spring Boot 3.x

- Spring Security (JWT + HttpOnly Cookies)

- Spring Data **JPA** (Hibernate)

### Infrastructure & Docs

- MySQL (Production) / H2 (Testing)

- Swagger / OpenAPI 3 (Springdoc)

- Docker & Maven

### Testing

- JUnit 5, Hamcrest

- Spring Security Test

---

## 🧪 Testing & Coverage

The project follows a comprehensive testing strategy to ensure reliability and security:

- **Unit Testing:** Focused on business logic validation in `Service` layers.
- **Integration Testing:** Each `Controller` is covered with integration tests using `@SpringBootTest` and `MockMvc`.
- **Security Testing:** Authentication and Role-Based Access Control (RBAC) are verified via `Spring Security Test`.
- **Database Testing:** H2 in-memory database is used for fast and isolated test execution.

### Test Coverage Summary

> [!IMPORTANT]
> **Current Status:** All controllers and core services are covered.
> ![Test Coverage Result](<img src="assets/test_1.png" width="45%" /> <img src="assets/test_2.png" width="45%" />)

---

## ⚙️ Installation & Setup

### 1. Clone the repository:

```bash
git clone [https://github.com/YOUR_USERNAME/bartrack-backend.git](https://github.com/YOUR_USERNAME/bartrack-backend.git)
```

### 2. Configure Environment:

Update application.properties or set env variables for jwt.secret and database credentials.

### 3. Build & Run:

```bash
mvn clean package
mvn spring-boot:run
```

### 4. Docker Setup:

```bash
docker build -t bartrack-backend .
docker run -p 8080:8080 bartrack-backend
```

### 📖 API Documentation:

Once the app is running, visit http://localhost:8080/swagger-ui.html

> This project uses **HttpOnly Cookies** for JWT storage. Since Swagger UI has known limitations with automatic cookie handling in some browser environments, you may experience `401 Unauthorized` errors even after a successful login.

## 🧠 Design Principles

- SOLID & DRY principles
- Clean Architecture (Layered)
- DTO Pattern for request/response decoupling
- Centralized Exception Handling
- Security-First development

---
