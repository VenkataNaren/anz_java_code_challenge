# ANZ Java Coding Challenge – Order Processing & Notification System

## 1. Overview

This project implements a simplified **Order Processing and Notification System** in **Java Spring Boot**.  
It exposes REST APIs to **create, retrieve, update, and search orders**, and notifies external systems on **order creation or status change**.

The project demonstrates:

- **Core Java concepts**
- **Spring Boot REST API development**
- **Spring Data JPA persistence**
- **Spring Security authentication**
- **Event-driven notifications**
- **Automated testing**
- **Lombok for reducing boilerplate code**

---

## 2. Features

### 2.1 Order API Management

- **Create a new order**: `POST /orders`
- **Retrieve order details**: `GET /orders/{id}`
- **Update order status**: `PUT /orders/{id}/status?status=COMPLETED`
- **Search orders**: `GET /orders` (returns all orders)

**Order Status Lifecycle:**

- `CREATED` (initial state)
- `COMPLETED`
- `CANCELLED`

---

### 2.2 Notifications

- Notifications are triggered on **order creation or status change**.
- Supports **multiple channels**:
  - Email (enabled by default)
  - SMS (configurable)

Configurable via `notification.properties`:

```properties
notification.email.enabled=true
notification.sms.enabled=false
```

- Uses `NotificationService` to simulate sending notifications.
- Designed for easy extension to real email/SMS systems or mocking with WireMock in tests.

---

## 3. Persistence

- Orders are persisted using **H2 in-memory database**.
- Spring Data JPA is used for repository operations.

H2 Console:  
`http://localhost:8080/h2-console`

**Database Configuration:**

```
URL: jdbc:h2:mem:ordersdb
Username: sa
Password: (empty)
```

---

## 4. Security

Spring Security **Basic Authentication** is enabled.

**Default Credentials:**

```
Username: admin
Password: admin123
```

Can be extended to OAuth2 or JWT for production use.

---

## 5. Error Handling

Consistent HTTP responses:

- **404 Not Found** → when order does not exist  
- **500 Internal Server Error** → unexpected exceptions  

Logging is currently done using `System.out.println()` (can be replaced with SLF4J/Logback).

Retry mechanism can be implemented for notifications using **Spring Retry** (dependencies included).

---

## 6. Testing

- Automated tests using **JUnit 5** & **MockMvc**
- Includes example test for order creation
- Spring Security test dependency included for secure endpoint testing

---

## 7. Technology Stack

| Layer        | Technology                     |
|--------------|--------------------------------|
| Framework    | Spring Boot 3.2                |
| Persistence  | Spring Data JPA, H2 Database   |
| Security     | Spring Security Basic Auth     |
| Build        | Maven                          |
| Java Version | 17                             |
| Utilities    | Lombok                         |
| Testing      | JUnit 5, MockMvc, WireMock     |

---

## 8. Key Technical Decisions

- **Lombok**: Reduce boilerplate for getters/setters/constructors.
- **H2 Database**: Lightweight, fast, ideal for testing.
- **Notification Service**: Multi-channel design, easily extendable.
- **Spring Security**: Basic Auth for demo; can upgrade to OAuth2/JWT.
- **Spring Retry & AOP**: Supports retrying failed notifications.
- **RESTful Design**: Standard HTTP verbs and status codes.

---

## 9. How to Run

### Clone the repository:

```bash
git clone https://github.com/VenkataNaren/anz_java_code_challenge.git
cd anz_java_code_challenge
```

### Run the project:

```bash
./mvnw spring-boot:run
```

### Access APIs using Basic Auth:

```
Username: admin
Password: admin123
```

---

## 10. Sample API Requests (cURL)

### Create Order

```bash
curl -u admin:admin123 -X POST \
  -H "Content-Type: application/json" \
  -d '{"description":"Test Order"}' \
  http://localhost:8080/orders
```

### Update Order Status

```bash
curl -u admin:admin123 -X PUT \
  http://localhost:8080/orders/1/status?status=COMPLETED
```

### Get Order

```bash
curl -u admin:admin123 http://localhost:8080/orders/1
```

### Search Orders

```bash
curl -u admin:admin123 http://localhost:8080/orders
```

---

## 11. How to Test

Run unit tests:

```bash
./mvnw test
```

Example test included for order creation.

---

## 12. Future Enhancements

- Integrate real email/SMS notification providers
- Add pagination and filtering to search API
- Implement OAuth2 / JWT authentication
- Add retry logic for failed notifications using Spring Retry

---

## 13. Contact / Author

**Author:** Venkata Narendra Kurapati  
**Email:** narendrakv1907@gmail.com
