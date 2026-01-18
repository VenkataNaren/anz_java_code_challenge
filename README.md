

---

# ANZ Java Coding Challenge – Order Processing & Notification System

## 1. Overview

This project implements a simplified **Order Processing and Notification System** in **Java Spring Boot**.  
It exposes REST APIs to **create, retrieve, update, and search orders**, and notifies external systems on **order creation or status change**.

The project demonstrates:

- Core Java concepts  
- Spring Boot REST API development  
- Spring Data JPA persistence  
- Spring Security authentication  
- Event‑driven notifications  
- Automated testing  

---

## 2. Features

### 2.1 Order API Management

- **Create a new order:** `POST /orders`  
- **Create multiple orders in bulk:** `POST /orders/bulkOrders`  
- **Retrieve order details:** `GET /orders/{id}`  
- **Update order status:** `PUT /orders/{id}/status?status=COMPLETED`  
- **Search orders:** `GET /orders` (supports pagination + optional status filtering)

#### **Order Status Lifecycle**

- `CREATED`  
- `COMPLETED`  
- `CANCELLED`

#### **Search Orders Parameters**

| Parameter | Description | Default |
|----------|-------------|---------|
| `page`   | Page number | `0` |
| `size`   | Page size | `10` |
| `status` | Filter by order status | Optional |

---

### 2.2 Notifications

Notifications are triggered on:

- **Order creation**
- **Order status change**

Supported channels:

- **Email** (enabled by default)
- **SMS** (configurable)

**Configuration (`application.properties` or `notification.properties`):**

```properties
notification.email.enabled=true
notification.sms.enabled=false
```

Notifications are simulated using `NotificationService`.  
Easily extendable to real email/SMS providers or WireMock.

---

## 3. Persistence

Orders are stored in an **H2 in‑memory database** using Spring Data JPA.

**H2 Console:**  
http://localhost:8080/h2-console

**Database Configuration:**

```
URL: jdbc:h2:mem:ordersdb
Username: sa
Password: sa
```

---

## 4. Security

Spring Security **Basic Authentication** is enabled.

**Default Credentials:**

```
Username: admin
Password: admin123
```

Can be extended to OAuth2 or JWT.

---

## 5. Error Handling

Consistent HTTP responses:

- **404 Not Found** → Order does not exist  
- **400 Bad Request** → Invalid input (e.g., invalid status)  
- **500 Internal Server Error** → Unexpected exceptions  

Logging currently uses `System.out.println()` (can be replaced with SLF4J/Logback).

Retry logic can be added using **Spring Retry**.

---

## 6. Swagger / OpenAPI Documentation

Swagger UI is included for interactive API exploration.

**Swagger UI:**

```
http://localhost:8080/swagger-ui.html
```

or

```
http://localhost:8080/swagger-ui/index.html
```

**OpenAPI JSON:**

```
http://localhost:8080/v3/api-docs
```

Swagger provides:

- Endpoint details  
- Request/response models  
- Parameter descriptions  
- HTTP status codes  
- Try‑it‑out functionality  

---

## 6.1 Web Client

A simple HTML page is available to interact with the Order API.

**URL:**  
http://localhost:8080/index.html

### Features

- Create a new order  
- Search and filter orders by status  
- Update order status via dropdown  

### Frontend Notes

- Input field uses `id="orderName"` → must map to backend `description` field  
- Uses `fetch()` to call REST endpoints  
- Handles pageable API responses from `/orders`

### Sample HTML Snippet

```html
<input type="text" id="orderName" placeholder="Order Name">
<button onclick="createOrder()">Create</button>

<select id="statusFilter">
  <option value="">All</option>
  <option value="CREATED">CREATED</option>
  <option value="COMPLETED">COMPLETED</option>
  <option value="CANCELLED">CANCELLED</option>
</select>
<button onclick="searchOrders()">Search</button>
```

---

## 7. Sample API Requests (cURL)

### Create Order

```bash
curl -u admin:admin123 -X POST \
  -H "Content-Type: application/json" \
  -d '{"description":"Test Order"}' \
  http://localhost:8080/orders
```

**Response:**

```json
{
  "id": 1,
  "description": "Test Order",
  "status": "CREATED"
}
```

---

### Get Order

```bash
curl -u admin:admin123 http://localhost:8080/orders/1
```

**Response:**

```json
{
  "id": 1,
  "description": "Test Order",
  "status": "CREATED"
}
```

---

### Search Orders

```bash
curl -u admin:admin123 "http://localhost:8080/orders?page=0&size=10&status=CREATED"
```

**Response:**

```json
{
  "content": [
    {
      "id": 1,
      "description": "Test Order",
      "status": "CREATED"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true
}
```

---

## 8. Testing

Automated tests using **JUnit 5** & **MockMvc**.

Includes tests for:

- Order creation  
- Order retrieval  
- Status updates  
- Search with pagination  

Run tests:

```bash
./mvnw test
```

---

## 9. Technology Stack

| Layer        | Technology |
|--------------|------------|
| Framework    | Spring Boot 3.2 |
| Persistence  | Spring Data JPA, H2 Database |
| Security     | Spring Security Basic Auth |
| Build        | Maven |
| Java Version | 17 |
| Utilities    | Lombok |
| Testing      | JUnit 5, MockMvc, WireMock |
| Documentation | Swagger / OpenAPI 3 |

---

## 10. Key Technical Decisions

- Lombok to reduce boilerplate  
- H2 Database for fast, in‑memory persistence  
- Notification Service designed for multi‑channel extensibility  
- Spring Security Basic Auth for simplicity  
- Spring Retry & AOP for retry logic  
- RESTful API design with proper HTTP semantics  
- Swagger/OpenAPI for documentation  
- Web client implemented as static HTML page  

---

## 11. How to Run

### Clone the repository

```bash
git clone https://github.com/VenkataNaren/anz_java_code_challenge.git
cd anz_java_code_challenge
```

### Run the project

```bash
./mvnw spring-boot:run
```

### Access APIs and UI

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html  
- **Web Client:** http://localhost:8080/index.html  
- **H2 Console:** http://localhost:8080/h2-console  

---

## 12. Future Enhancements

- Integrate real email/SMS providers  
- Add advanced pagination, sorting, filtering  
- Implement OAuth2 / JWT authentication  
- Add retry logic for failed notifications  
- Replace `System.out.println()` with SLF4J/Logback  
- Add API versioning  

---

## 13. Contact / Author

**Author:** Venkata Narendra Kurapati  
**Email:** narendrakv1907@gmail.com

---
