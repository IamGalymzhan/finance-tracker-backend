# 💸 Personal Finance Tracker Backend

A powerful, customizable backend system to track, organize, and analyze personal finances — built with Spring Boot,
designed for real-life banking data.

*Built from scratch to solve my own problem — not just another demo project.*

---

## 🌱 Motivation

I built this project out of personal need — existing finance tools didn’t give me enough control, clarity, or
customization. I wanted something tailored to how *I* actually manage and think about my finances — and this tracker
became my solution.

Along the way, I used it as an opportunity to deepen my backend engineering skills: building a production-grade Spring
Boot system, integrating security, caching, and testability from the ground up.

A key feature I’m currently working on (and excited to finish soon) is **automated parsing of PDF banking reports** from
popular local banks — turning raw bank exports into categorized, editable financial operations.

---

## 🛠️ Tech Stack

- **Java 21 + Spring Boot** – Backend framework for building RESTful services
- **Spring Security + JWT** – Authentication and authorization with stateless sessions
- **Hibernate (JPA)** – ORM layer for working with relational data
- **PostgreSQL** – Primary database for persistent storage
- **Redis** – Used for caching frequently accessed data
- **Docker Compose** – Containerized local environment (app + Redis + PostgreSQL)
- **H2** – In-memory database used for isolated testing
- **JUnit 5 + Spring Test** – Unit and integration testing support

---

## ✨ Features

> Built to be practical, extendable, and user-friendly — with real-world usage in mind.

- ✅ **JWT-secured user system** – Users can register, log in, and manage their data securely
- ✅ **CRUD for Accounts and Operations** – Manage multiple accounts and financial operations
- ✅ **Advanced Filtering** – Filter operations by date range, amount, account, and more
- ✅ **CSV Export** – Download selected operations for external analysis or reporting
- ✅ **Redis Caching** – Faster repeated queries for accounts and operations
- ✅ **Interactive Swagger UI** – Test and explore the API from your browser
- ✅ **Summary Endpoint** – Total spending/income grouped by category
- ✅ **Bank PDF Parser** – Automatically extract and import data from banking summaries
- ✅ **Background Tasks** – Process and categorize operations in the background
- ✅ **User Tagging Rules** – Auto-tag operations based on custom user preferences (e.g., if name contains "Uber" tag
  as "Taxi")

---

## 🚀 Getting Started

This project runs as a self-contained backend using Docker Compose. You'll need:

- [Docker](https://www.docker.com/)
- [Java 21](https://jdk.java.net/21/)
- [Postman](https://www.postman.com/) (optional, for testing the API)

### 🔧 Local Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/finance-tracker-backend.git
   cd finance-tracker-backend
    ```

2. **Start the backend with Docker Compose**
    ```bash
   docker-compose up -d --build
    ```
3. **The application should be running at:**
   ```bash
   http://localhost:8080
    ```

### 📬 Useful Links

- **Swagger API Docs**:  
  [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

- **Health Check Endpoint**:  
  [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

- **Postman Collection**:  
  You can import the collection from:  
  [`/docs/FinanceTracker.postman_collection.json`](docs/FinanceTracker.postman_collection.json)

> ⚠️ You may need to register a user and authenticate with a JWT token before using most endpoints.

---

## 🛣️ Roadmap & Future Plans

Here’s what I’m actively planning or considering for upcoming versions:

- 🧠 **PDF Report Parser** – Automatically read and parse bank statements (Kazakhstani banks)
- ⚙️ **Background Job Processing** – Auto-categorize and tag operations after creation
- 🏷️ **User Tagging Rules UI** – Let users define tagging rules via API or frontend
- 🌍 **Frontend (React or Next.js)** – Planned for full user interface experience

> If you're reviewing this for an internship or job application: many of these are actively in progress, with some
> experimental branches already in place.

---

## 👤 Author

**Galymzhan Zhangazy**  
Backend Developer · Computer Science Student at Nazarbayev University  
[GitHub](https://github.com/IamGalymzhan) | [LinkedIn](https://www.linkedin.com/in/galymzhan-zhangazy/)

> Feel free to reach out if you have feedback

