# BiteReview-API

## Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
3. [Installation](#installation)
4. [API Testing with cURL](#api-testing-with-curl)
5. [Technologies Used](#technologies-used)
6. [Acknowledgements](#acknowledgements)

## Introduction

**BiteReview-API** is a RESTful service designed to facilitate user interactions with restaurant reviews. It allows users to create profiles, add restaurants, submit dining reviews, and manage user information. The API supports functionalities such as retrieving pending reviews, approving or rejecting reviews, and searching for restaurants based on zip code and allergy scores.

## Features

- **User Management**: Create and update user profiles.
- **Restaurant Management**: Add new restaurants to the database.
- **Review System**: Submit, approve, reject, and retrieve dining reviews, including optional commentary and allergy scores for peanut, egg, and dairy.
- **Search Functionality**: Search for restaurants by zip code and allergy scores.
- **Admin Controls**: Manage pending reviews and update their statuses.

## Installation

To set up the **BiteReview-API** on your local machine, ensure you have the following prerequisites:

- **Java 17** or higher
- **Maven 3.x** or higher

**Steps:**

1. **Clone the repository**:
    ```bash
    git clone https://github.com/irened123/BiteReview-API.git
    ```

2. **Navigate to the project directory**:
    ```bash
    cd BiteReview-API
    ```

3. **Build the project**:
    ```bash
    mvn clean package
    ```

4. **Run the application**:
    ```bash
    java -jar target/bitereviewapi-0.0.1-SNAPSHOT.jar
    ```

The application will start on `http://localhost:8080`.

## API Testing with cURL

Below are examples of how to interact with the **BiteReview-API** using cURL commands. For additional endpoints and detailed testing examples, please refer to the [API Testing with cURL] (./docs/api-testing-curl.md) documentation.

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

## Technologies Used

- **Spring Boot**: Framework for building stand-alone, production-ready Spring applications with minimal configuration.
- **Spring Data JPA**: Simplifies data access by providing a repository abstraction over JPA implementations, reducing boilerplate code.
- **H2 Database**: Lightweight, in-memory database ideal for development and testing, allowing rapid prototyping without the need for external database setup.
- **Maven**: Build automation tool that manages project dependencies and the build lifecycle, ensuring consistent and reproducible builds.
- **Lombok**: Reduces boilerplate code in Java by automatically generating getters, setters, constructors, and more through simple annotations.


## Acknowledgements

This project follows [Codecademy's Dining Review API assignment] (https://www.codecademy.com/projects/portfolio/dining-review-api), which provided valuable guidance and helped me strengthen my development skills.

