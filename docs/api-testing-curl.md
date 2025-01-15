# API Testing with cURL

This document outlines the cURL commands used to test the RESTful API, along with their respective responses.

---

### 1. Create a New User

**Command:**

```bash
curl -X POST http://localhost:8080/api/users \
     -H "Content-Type: application/json" \
     -d '{"displayName": "john_doe", "city": "Boston", "state": "MA", "zipCode": "02118"}'
```

**Response:**

```json
{"message": "User profile created successfully"}
```

### 2. Add a New Restaurant 

**Command:**

```bash
curl -X POST http://localhost:8080/api/restaurants \
     -H "Content-Type: application/json" \
     -d '{"name": "Best Diner", "zipCode": "02118"}'
```

**Response:**

```json
{"message": "Restaurant added successfully"}
```

### 3. Submit a New Dining Review

**Command:**

```bash
curl -X POST http://localhost:8080/api/reviews \
     -H "Content-Type: application/json" \
     -d '{"displayName": "john_doe", "restaurantId": 1, "peanutScore": 4, "dairyScore": 5, "eggScore": 3, "commentary": "Great food!"}'
```

**Response:**

```json
{"message": "Review submitted successfully"}
```

### 4. Fetch All Pending Reviews

**Command:**

```bash
curl -X GET http://localhost:8080/api/admin/reviews/pending
```

**Response:**

```json
{"pendingReviews": [{"id": 1,"displayName": "john_doe","restaurantId": 1, "peanutScore": 4,"eggScore": 3,"dairyScore": 5,"commentary": "Great food!", "status":"PENDING"}]}
```

### 5. Approve a Dining Review

**Command:**

```bash
curl -X PUT http://localhost:8080/api/admin/reviews/1 \
     -H "Content-Type: application/json" \
     -d '{"accepted": true}'
```

**Response:**

```json
{"message": "Review status updated successfully"}
```

### 6. Retrieve Approved Reviews for a Restaurant

**Command:**

```bash
curl -X GET "http://localhost:8080/api/reviews/restaurant/1?status=APPROVED"
```

**Response:**

```json
[{"id": 1, "displayName":"john_doe", "restaurantId": 1, "peanutScore": 4,"eggScore": 3, "dairyScore": 5, "commentary": "Great food!", "status": "APPROVED"}]
```

### 7. Update User Information

**Command:**

```bash
curl -X PUT http://localhost:8080/api/users/john_doe \
     -H "Content-Type: application/json" \
     -d '{"city": "Cambridge", "state": "MA"}'
```

**Response:**

```json
{"message": "User information updated successfully"}
```

### 8. Retrieve User Profile by Display Name

**Command:**

```bash
curl -X GET http://localhost:8080/api/users/john_doe
```

**Response:**

```json
{"displayName": "john_doe", "city": "Cambridge","state": "MA","zipCode": "02118"}
```

### 9. Search Restaurants by Zip Code and Allergy Score

**Command:**

```bash
curl -X GET "http://localhost:8080/api/restaurants/search?zipcode=02118&allergy=peanut"
```

**Response:**

```json
[{"id":1,"name":"Best Diner","zipCode":"02118","peanutScore":4.0,"eggScore":3.0,"dairyScore":5.0,"overallScore":4.0}]
```

### 10. Reject a Dining Review

**Command:**

```bash
curl -X PUT http://localhost:8080/api/admin/reviews/1 \
     -H "Content-Type: application/json" \
     -d '{"accepted": false}'
```

**Response:**

```json
{"message":"Review status updated successfully"}
```





























