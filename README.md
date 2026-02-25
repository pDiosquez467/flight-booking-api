# ✈️ Flight Booking API

[cite_start]Backend project built with **Java** and **Spring Boot** that simulates a flight booking system[cite: 1, 18].

[cite_start]This project focuses on **domain modeling and object-oriented design**, rather than just exposing endpoints[cite: 1, 9]. [cite_start]The goal is to design a system where business rules live inside the domain model and invariants are protected[cite: 11, 12].

---

## 🎯 Purpose of the Project

Instead of starting from controllers or database structure, this project begins with:

- [cite_start]**Defining domain entities** (`Flight`, `Booking`, `Passenger`) [cite: 1]
- [cite_start]**Modeling business rules explicitly** [cite: 1, 2]
- [cite_start]**Protecting invariants inside the domain** [cite: 6, 12]
- [cite_start]**Keeping services thin** and focused on orchestration [cite: 6]

The intention is to build the system incrementally and evolve it through different versions.

---

## 🗺️ Roadmap & Architecture Evolution

### Version 1: Core Domain & OOP Purity (Current)
[cite_start]**Focus:** Establishing a Rich Domain Model and enforcing business rules strictly within entities to avoid "Anemic Models"[cite: 1, 11].
* [cite_start]**Rich Domain Model:** Business logic (capacity checks, cancellation windows) is encapsulated within `Flight` and `Booking` entities, not scattered in services[cite: 1, 9, 15].
* **Concurrency Control:** Implementation of **Optimistic Locking** (`@Version`) to prevent race conditions (overselling) without performance-heavy database locks.
* [cite_start]**Persistence Strategy:** **H2 Database** (In-Memory) for rapid prototyping, using full **Spring Data JPA** repositories to ensure easy migration to SQL later[cite: 18].
* [cite_start]**Error Handling:** Use of **Domain Exceptions** (Fail-Fast principle) rather than boolean returns for business rule violations[cite: 8].
* **Basic Containerization:** Docker support for reproducible runtime environments.

### Version 2: Persistence & API Contract (Next Step)
**Focus:** Transitioning from In-Memory prototyping to a robust, containerized architecture with production-grade persistence.
* **PostgreSQL Implementation:** Replacing H2 with a real relational database running in a Docker container.
* **Infrastructure as Code:** Using **Docker Compose** to orchestrate the API and Database services together.
* **Database Migrations:** Integrating **Flyway** to manage schema versioning (SQL scripts) and ensure reproducible database states across environments.
* **REST Layer:** Full implementation of `@RestController`, separating the API contract from the domain logic.
* **DTO Pattern:** Decoupling internal entities from external API representations using Data Transfer Objects (DTOs) and proper validation layers (`@Valid`).
* **API Documentation:** Integration of **Swagger / OpenAPI** for interactive API exploration and testing.

### Version 3: Security & Production Readiness (Future)
**Focus:** Hardening the application for a production-like environment with security and automation.
* **Security Framework:** Implementing **Spring Security** with **JWT** (JSON Web Tokens) for stateless authentication.
* **Role-Based Access Control (RBAC):** Defining permissions hierarchy (e.g., `ADMIN` for flight management vs `CUSTOMER` for booking).
* **CI/CD Pipelines:** Setting up **GitHub Actions** to automatically build and run Unit Tests on every push to `main`.
* **Observability:** Adding **Spring Actuator** for health checks and structured logging for better debugging.
* **Load Testing:** Refining concurrency strategies and conducting stress tests to verify data consistency under high load.

---

## 🚀 Tech Stack

- **Java 21**
- **Spring Boot 3.x**
- [cite_start]**Spring Data JPA** [cite: 18]
- [cite_start]**H2 Database** [cite: 18]
- **Hibernate**
- **Maven**

---

## 📌 Project Status

**Active development.** The project is being built incrementally, documenting design decisions along the way.

---

## 🧠 Why this project?

This repository represents a journey from "making it work" to **"engineering it right"**. It serves as a practical lab to apply advanced concepts in a controlled environment, focusing on:

* **Beyond CRUD:** Moving away from simple data manipulation to solving actual business problems (concurrency, race conditions, and state management).
* [cite_start]**Rich Domain Modeling:** Implementing logic where it belongs—inside the entities—to protect business invariants and avoid the "Anemic Domain Model" anti-pattern[cite: 11, 12].
* **Evolutionary Architecture:** Starting with a clean Monolith and progressively refactoring towards a more complex, distributed system only when necessary.
* **Engineering Trade-offs:** Documenting the *why* behind every technical decision (e.g., Optimistic Locking vs. Pessimistic Locking, Monolith vs. Microservices).

---

## 🤝 Feedback & Contributions

I am building this project in public because I believe that code reviews and constructive criticism are the fastest way to grow as an engineer.

If you spot an anti-pattern, a potential bug, or just have a better way to solve a specific problem, **I want to hear it**.

* **Found a bug?** Open an issue.
* **Have a suggestion on the design?** Feel free to reach out or comment on my commits.
* **Want to discuss the implementation?** Let's connect on LinkedIn!

I am actively looking for feedback from experienced developers to help me polish my understanding of Enterprise Java development.

👉 [Connect on LinkedIn](https://www.linkedin.com/in/pablodiosquez/)
