
# 🍸 BarTrack Backend

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Security](https://img.shields.io/badge/Security-JWT-orange)
![Database](https://img.shields.io/badge/Database-H2%20%7C%20MySQL-lightgrey)
![Build](https://img.shields.io/badge/Build-Maven-red)
![Status](https://img.shields.io/badge/Status-MVP%20Complete-success)

---

## 📌 Project Overview

**BarTrack Backend** is a RESTful API designed to manage bar inventory and sales operations.

This project focuses on:

- Secure authentication and authorization
- Role-based access control
- Inventory management
- Order (check) creation and tracking

The current implementation represents a **Minimum Viable Product (MVP)** with a clean architecture prepared for enterprise-level scaling.

---

## 🎯 MVP Scope

### Admin can:
- Full CRUD management of items
- View all orders
- Manage system catalog

### User can:
- View active items
- Create orders
- View own orders

### Not included in MVP (planned future features):
- Cocktail recipes
- Ingredient tracking
- Advanced stock consumption logic
- Order status lifecycle

---

## 🏗️ Architecture

The project follows layered architecture:

Controller → Service → Repository → Database

### Key Layers:

- **Controller Layer** – Handles HTTP requests
- **Service Layer** – Business logic & validation
- **Repository Layer** – Data access using JPA
- **Security Layer** – JWT authentication & authorization
- **Exception Layer** – Global error handling
- **DTO Layer** – Data transfer and validation

---

## 🗄️ Database Model (MVP)

### Key Layers:

- **Controller Layer** – Handles HTTP requests
- **Service Layer** – Business logic & validation
- **Repository Layer** – Data access using JPA
- **Security Layer** – JWT authentication & authorization
- **Exception Layer** – Global error handling
- **DTO Layer** – Data transfer and validation

---

## 🗄️ Database Model (MVP)

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


    
