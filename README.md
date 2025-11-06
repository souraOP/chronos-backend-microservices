<div align="center">

# Chronos Microservices

### Enterprise-Grade Employee Leave & Attendance Management System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-blue.svg)](https://spring.io/projects/spring-cloud)
[![Microservices](https://img.shields.io/badge/Architecture-Microservices-blueviolet.svg)](https://microservices.io/)
[![License](https://img.shields.io/badge/License-Apache%202.0-yellow.svg)](https://www.apache.org/licenses/LICENSE-2.0)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Microservices](#-microservices)
- [Technology Stack](#-technology-stack)
- [Dependencies](#-dependencies)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Configuration](#-configuration)
- [Contributing](#-contributing)
- [Author](#-author)
- [License](#-license)

---

## 🎯 Overview

**Chronos** is a comprehensive, cloud-native Employee Leave and Attendance Management System (ELAMS) built using modern microservice's architecture. The system provides robust solutions for managing employee attendance, leave requests, shift scheduling, employee data, and comprehensive reporting capabilities.

### ✨ Key Features

- 🔐 **Secure Authentication & Authorization** with JWT
- 👥 **Employee Management** with role-based access control
- 📅 **Leave Management** with approval workflows
- ⏱️ **Attendance Tracking** with real-time monitoring
- 🔄 **Shift Management** for flexible scheduling
- 📊 **Advanced Reporting** and analytics
- 🌐 **API Gateway** for unified access
- 🔍 **Service Discovery** with Eureka
- ⚙️ **Centralized Configuration** management
- 📈 **Health Monitoring** and actuator endpoints
- 🛡️ **Circuit Breaker** pattern for resilience

---

## 🏗️ Architecture

Chronos follows a **distributed microservices architecture** with the following components:

```
┌─────────────────────────────────────────────────────────────┐
│                        API Gateway                          │
│                   (Port: 8080)                             │
└────────────────────────┬────────────────────────────────────┘
                         │
         ┌───────────────┴───────────────┐
         │                               │
    ┌────▼─────┐                   ┌────▼────┐
    │  Eureka  │                   │ Config  │
    │  Server  │                   │ Server  │
    └────┬─────┘                   └─────────┘
         │
    ┌────┴──────────────────────────────────────┐
    │                                           │
┌───▼───┐ ┌──────────┐ ┌────────┐ ┌──────────┐ ┌────────┐
│ Auth  │ │Employee  │ │ Leave  │ │Attendance│ │ Shift  │
│Service│ │ Service  │ │Service │ │ Service  │ │Service │
└───────┘ └──────────┘ └────────┘ └──────────┘ └────────┘
                             │
                        ┌────▼────┐
                        │ Report  │
                        │ Service │
                        └─────────┘
```

---

## 🔧 Microservices

### 🌐 **API Gateway** (Port: 8080)
Central entry point for all client requests with reactive programming support.

**Key Features:**
- Reactive routing with Spring Cloud Gateway WebFlux
- JWT-based authentication filter
- Request/response logging
- Rate limiting capabilities
- Spring Boot Admin integration

**Dependencies:**
- Spring Cloud Gateway WebFlux
- Spring Security
- Spring Boot Admin Server
- Spring Boot Actuator
- SpringDoc OpenAPI (WebFlux)
- JJWT (JWT processing)

---

### 🔍 **Eureka Server** (Service Registry)
Service discovery and registration server for all microservices.

**Key Features:**
- Dynamic service registration
- Load balancing
- Health checks
- Service instance management

**Dependencies:**
- Spring Cloud Netflix Eureka Server
- Spring Boot Actuator

---

### ⚙️ **Config Server** (Configuration Management)
Centralized configuration management for all microservices.

**Key Features:**
- Externalized configuration
- Environment-specific properties
- Configuration refresh without restart
- Git/File system backend support

**Dependencies:**
- Spring Cloud Config Server
- Spring Cloud Netflix Eureka Client
- Spring Boot Actuator

---

### 🔐 **Auth Service** (Authentication & Authorization)
Handles user authentication, authorization, and JWT token management.

**Key Features:**
- User registration and login
- JWT token generation and validation
- Password encryption with BCrypt
- Role-based access control (RBAC)
- Refresh token mechanism

**Dependencies:**
- Spring Boot Web
- Spring Security
- Spring Data JPA
- Spring Validation
- Spring Cloud OpenFeign
- Resilience4j Circuit Breaker
- JJWT (v0.12.6)
- SpringDoc OpenAPI
- MySQL Connector
- Lombok
- MapStruct

**Tech Stack:**
- Authentication: JWT (JSON Web Tokens)
- Database: MySQL/PostgresSQL
- ORM: Spring Data JPA with Hibernate

---

### 👥 **Employee Service** (Employee Management)
Manages employee information, profiles, and organizational hierarchy.

**Key Features:**
- CRUD operations for employee data
- Employee profile management
- Department and role management
- Employee search and filtering
- Organizational hierarchy

**Dependencies:**
- Spring Boot Web
- Spring Data JPA
- Spring Security
- Spring Cloud OpenFeign
- Spring Validation
- Resilience4j Circuit Breaker
- SpringDoc OpenAPI
- MySQL Connector
- Lombok
- MapStruct
- Spring Boot DevTools

---

### 📅 **Leave Service** (Leave Management)
Handles employee leave requests, approvals, and leave balance tracking.

**Key Features:**
- Leave application submission
- Multi-level approval workflow
- Leave balance calculation
- Leave type management (Sick, Casual, Annual, etc.)
- Leave history tracking
- Calendar integration

**Dependencies:**
- Spring Boot Web
- Spring Data JPA
- Spring Security
- Spring Cloud OpenFeign
- Spring Validation
- Resilience4j Circuit Breaker
- SpringDoc OpenAPI
- MySQL Connector
- Lombok
- MapStruct
- Spring Boot DevTools

---

### ⏱️ **Attendance Service** (Attendance Tracking)
Real-time attendance tracking and monitoring system.

**Key Features:**
- Clock in/out functionality
- Location-based attendance
- Real-time attendance monitoring
- Attendance regularization
- Late arrival/early departure tracking
- Overtime calculation

**Dependencies:**
- Spring Boot Web
- Spring Data JPA
- Spring Security
- Spring Cloud OpenFeign
- Spring Validation
- Resilience4j Circuit Breaker
- SpringDoc OpenAPI
- MySQL Connector
- Lombok
- MapStruct
- Spring Boot DevTools

---

### 🔄 **Shift Service** (Shift Management)
Manages employee work shifts and scheduling.

**Key Features:**
- Shift creation and assignment
- Shift rotation management
- Flexible shift timings
- Shift swap requests
- Holiday and weekend shift management
- Shift roster generation

**Dependencies:**
- Spring Boot Web
- Spring Data JPA
- Spring Security
- Spring AOP
- Spring Cloud OpenFeign
- Spring Validation
- Resilience4j Circuit Breaker
- SpringDoc OpenAPI
- MySQL Connector
- Lombok
- MapStruct

---

### 📊 **Report Service** (Analytics & Reporting)
Generates comprehensive reports and analytics.

**Key Features:**
- Attendance reports
- Leave analytics
- Employee performance metrics
- Custom report generation
- Data export (PDF, Excel)
- Dashboard analytics

**Dependencies:**
- Spring Boot Web
- Spring Data JPA
- Spring Security
- Spring Cloud OpenFeign
- Spring Validation
- Resilience4j Circuit Breaker
- SpringDoc OpenAPI
- MySQL Connector
- Lombok
- MapStruct
- Spring Boot DevTools

---

### 📚 **Common Library** (Shared Components)
Reusable components shared across all microservices.

**Key Features:**
- Common exception handling
- Global error responses
- Shared DTOs and utilities
- Custom validators
- Response wrappers

**Dependencies:**
- Spring Boot AutoConfigure
- Spring Web
- Spring Validation
- Jackson Annotations

---

## 🛠️ Technology Stack

### Core Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 21 | Programming Language |
| **Spring Boot** | 3.5.7 | Application Framework |
| **Spring Cloud** | 2025.0.0 | Microservices Framework |
| **Maven** | 3.x | Build Tool |

### Spring Cloud Components

- **Spring Cloud Gateway** - API Gateway (Reactive)
- **Spring Cloud Netflix Eureka** - Service Discovery
- **Spring Cloud Config** - Configuration Management
- **Spring Cloud OpenFeign** - Declarative REST Client
- **Resilience4j** - Circuit Breaker & Fault Tolerance

### Data & Persistence

- **Spring Data JPA** - Data Access Layer
- **Hibernate** - ORM Framework
- **MySQL Connector** - Database Driver
- **JPA Auditing** - Automatic audit fields

### Security

- **Spring Security** - Authentication & Authorization
- **JJWT (v0.12.6)** - JWT Token Management
- **BCrypt** - Password Hashing

### Documentation

- **SpringDoc OpenAPI (v2.8.13)** - API Documentation
- **Swagger UI** - Interactive API Explorer

### Utilities & Tools

- **Lombok** - Boilerplate Code Reduction
- **MapStruct** - Bean Mapping
- **Spring Boot DevTools** - Development Productivity
- **Spring Boot Actuator** - Monitoring & Management
- **Spring Boot Admin** - Admin UI for Spring Boot Applications

### Testing

- **Spring Boot Test** - Testing Framework
- **JUnit 5** - Unit Testing
- **Mockito** - Mocking Framework
- **Maven Surefire Plugin (v3.2.5)** - Test Execution

---

## 📦 Dependencies

### Common Dependencies Across Services

```xml
<!-- Spring Cloud Configuration -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>

<!-- Service Discovery -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

<!-- Resilience & Fault Tolerance -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>

<!-- Inter-Service Communication -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

<!-- API Documentation -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-api</artifactId>
    <version>2.8.13</version>
</dependency>

<!-- Monitoring -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Data Persistence -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### Specialized Dependencies

#### JWT Processing (Auth Service & API Gateway)
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

#### Reactive Stack (API Gateway)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-gateway-server-webflux</artifactId>
</dependency>
```

#### Admin UI (API Gateway)
```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-server</artifactId>
    <version>3.5.5</version>
</dependency>
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **MySQL 8.0+** (or your preferred database)
- **Git**
- **IDE** (IntelliJ IDEA, Eclipse, VS Code)

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/souraOP/chronos-backend-microservices.git
   cd chronos-backend-microservices
   ```

2. **Configure Database**

   Create databases for each service:
   ```sql
   CREATE DATABASE chronos_auth;
   CREATE DATABASE chronos_employee;
   CREATE DATABASE chronos_leave;
   CREATE DATABASE chronos_attendance;
   CREATE DATABASE chronos_shift;
   CREATE DATABASE chronos_report;
   ```

3. **Update Configuration**

   Update `application.properties` or `application.yml` for each service with your database credentials.

4. **Build the project**
   ```bash
   mvn clean install
   ```

### Running the Services

**Important:** Start services in the following order:

1. **Config Server** (Port: 8888)
   ```bash
   cd config-server
   mvn spring-boot:run
   ```

2. **Eureka Server** (Port: 8761)
   ```bash
   cd eureka-server
   mvn spring-boot:run
   ```

3. **API Gateway** (Port: 8080)
   ```bash
   cd api-gateway
   mvn spring-boot:run
   ```

4. **Business Services** (parallel startup possible)
   ```bash
   # Auth Service (Port: 8081)
   cd auth-service && mvn spring-boot:run &
   
   # Employee Service (Port: 8082)
   cd employee-service && mvn spring-boot:run &
   
   # Leave Service (Port: 8083)
   cd leave-service && mvn spring-boot:run &
   
   # Attendance Service (Port: 8084)
   cd attendance-service && mvn spring-boot:run &
   
   # Shift Service (Port: 8085)
   cd shift-service && mvn spring-boot:run &
   
   # Report Service (Port: 8086)
   cd report-service && mvn spring-boot:run &
   ```

### Running All Services with Maven (Root Level)

```bash
mvn clean install
mvn spring-boot:run
```

---

## 📚 API Documentation

Once all services are running, access the API documentation:

### Swagger UI Endpoints

- **API Gateway**: http://localhost:8080/swagger-ui.html
- **Auth Service**: http://localhost:8081/swagger-ui.html
- **Employee Service**: http://localhost:8082/swagger-ui.html
- **Leave Service**: http://localhost:8083/swagger-ui.html
- **Attendance Service**: http://localhost:8084/swagger-ui.html
- **Shift Service**: http://localhost:8085/swagger-ui.html
- **Report Service**: http://localhost:8086/swagger-ui.html

### Service Registry

- **Eureka Dashboard**: http://localhost:8761

### Admin Console

- **Spring Boot Admin**: http://localhost:8080/admin

---

## ⚙️ Configuration

### Environment Variables

Set the following environment variables or update `application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/chronos_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# Eureka Server
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# Config Server
spring.cloud.config.uri=http://localhost:8888

# JWT Secret
jwt.secret=your-256-bit-secret-key
jwt.expiration=86400000
```

### Actuator Endpoints

Health check and monitoring endpoints are available at:

```
http://localhost:{port}/actuator/health
http://localhost:{port}/actuator/info
http://localhost:{port}/actuator/metrics
```

---

## 🔒 Security

### Authentication Flow

1. User sends login credentials to `/api/auth/login`
2. Auth Service validates credentials and generates JWT token
3. Client includes JWT token in `Authorization: Bearer <token>` header
4. API Gateway validates token for all subsequent requests
5. Authorized requests are forwarded to respective microservices

### Security Features

- ✅ JWT-based stateless authentication
- ✅ Role-based access control (RBAC)
- ✅ Password encryption with BCrypt
- ✅ Secure inter-service communication
- ✅ CORS configuration
- ✅ Request validation

---

## 🧪 Testing

Run tests for all modules:

```bash
mvn test
```

Run tests for a specific service:

```bash
cd auth-service
mvn test
```

---

## 📊 Project Structure

```
chronos-backend-microservices/
│
├── api-gateway/              # API Gateway Service
├── eureka-server/            # Service Registry
├── config-server/            # Configuration Server
├── auth-service/             # Authentication Service
├── employee-service/         # Employee Management Service
├── leave-service/            # Leave Management Service
├── attendance-service/       # Attendance Tracking Service
├── shift-service/            # Shift Management Service
├── report-service/           # Reporting Service
├── common-lib/               # Shared Library
├── pom.xml                   # Parent POM
└── README.md                 # Project Documentation
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Coding Standards

- Follow Java naming conventions
- Write meaningful commit messages
- Add unit tests for new features
- Update documentation as needed

---

## 👨‍💻 Author

**Sourasish Mondal**

- Portfolio: [https://soura.netlify.app/](https://soura.netlify.app/)
- Email: sourasish.mondal@cognizant.com
- GitHub: [@souraOP](https://github.com/souraOP)

---

## 📄 License

This project is licensed under the **CCO 1.0 Universal** - see the [LICENSE](https://www.apache.org/licenses/LICENSE-2.0.txt) file for details.

```
Copyright 2025 Sourasish Mondal

    CREATIVE COMMONS CORPORATION IS NOT A LAW FIRM AND DOES NOT PROVIDE
    LEGAL SERVICES. DISTRIBUTION OF THIS DOCUMENT DOES NOT CREATE AN
    ATTORNEY-CLIENT RELATIONSHIP. CREATIVE COMMONS PROVIDES THIS
    INFORMATION ON AN "AS-IS" BASIS. CREATIVE COMMONS MAKES NO WARRANTIES
    REGARDING THE USE OF THIS DOCUMENT OR THE INFORMATION OR WORKS
    PROVIDED HEREUNDER, AND DISCLAIMS LIABILITY FOR DAMAGES RESULTING FROM
    THE USE OF THIS DOCUMENT OR THE INFORMATION OR WORKS PROVIDED
    HEREUNDER.
```

---

## 🌟 Acknowledgments

- Spring Boot Team for the excellent framework
- Spring Cloud Team for microservices components
- OpenAPI & Swagger for API documentation tools
- All contributors and supporters

---

<div align="center">

### ⭐ Star this repository if you find it helpful!

**Made with ❤️ by [Sourasish Mondal](https://soura.netlify.app/)**

</div>