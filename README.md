# Wigell Gym Service 
[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
##### 🏋️ Overview:
Wigell Gym Service is a microservice for managing gym bookings, workouts, and instructors, designed to integrate with the WigellGateway. 
It enables admins to manage workouts and instructors, and users to book, view, and cancel bookings on workouts.
Currency conversion (SEK to EURO) is supported via an external API.
---

## 🧩 Related projects
- **[WigellGateway]** (https://github.com/SaraSnail/WigellGateway) - Main entry point and API Gateway for this microservice.

---

## 🚀 Tech Stack

- **Language :** Java 21
- **Build Tool :** Apache Maven 
- **Framework :** Spring boot 3.5.5
  - Spring Data JPA
  - Spring Web
- **Database :** MYSQL 8.0 (Docker container)
- **Security :** Spring Security
- **Logging :** Log4j2, Jansi 2.4.0
- **External API :**  [API Plugin Currency](https://apiplugin.com/) (SEK ⇒ EURO)
- **Testing :** Spring Boot Testing, H2 (test DB), Mockito

---

## 🏁 Getting started
### Prerequisites

- [Java 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- [Maven](https://maven.apache.org/)
- [Docker](https://www.docker.com/) (for running MySQL and containerizing the service)
---

### 🔌 Ports
- **Gym Service :** `6565`
- **MySQL :** `3306`
- **Gateway :** `4545`

---

## 🔒 Authentication

Some endpoints require authentication and different permissions (admin vs. user). 

**ADMIN:**
- Username: simon
- Password: simon

**USER:**
- Username: alex
- Password: alex


- Username: sara
- Password: sara


- Username: amanda
- Password: amanda

---

## 📚 API Endpoints

### Admin:
* **GET** `/api/wigellgym/listcanceled` - Lists all canceled bookings
* **GET** `/api/wigellgym/listupcoming` - Lists all upcoming workouts with active bookings
* **GET** `/api/wigellgym/listpast` - Lists all bookings that weren't cancelled on workouts that has already happened
* **POST** `/api/wigellgym/addworkout/{instructorId}` - Creates a new workout with given instructor
* **PUT** `/api/wigellgym/updateworkout/{workoutId}/{instructorId}` - Updates the filled in fields and can change instructor with _instructorId_
* **PUT** `/api/wigellgym/remworkout/{workoutId}` - Updates workout so it's inactive and can't be booked. Also cancels all bookings if workout has not happened yet
* **POST** `/api/wigellgym/addinstructor` - Creates a new instructor
* **GET** `/api/wigellgym/instructors` - Gets all instructors

### User:
* **GET** `/api/wigellgym/workouts` - Lists all active, future and not fully booked workouts
* **GET**` /api/wigellgym/mybookings` - Get the users bookings, both active and inactive
* **POST** `/api/wigellgym/bookworkout/{workoutId}` - Books workout on the user
* **PUT** `/api/wigellgym/cancelworkout/{bookingId}` - Cancels the booking on the workout. Gets stopped if ex workout is less than 24 hours away
* **GET** `/api/wigellgym/instructors` - Gets all active instructors

---

## MySQL
- Database name: wigelldb
- Username: wigelldbassa
- Password: assa

---

## 🐳 Docker
- The service is containerized with a custom timezone (Europe/Stockholm).
- Use `script.bat` to build and run the container easily.
- Docker network: `wigell-network`
- Database container name: `wigell-mysql-service`

### Create MySQL container
Run this command:
```bash
docker run -d -p 3307:3306 --name wigell-mysql-service --network wigell-network -e MYSQL_ROOT_PASSWORD=assa -e MYSQL_DATABASE=wigelldb -e MYSQL_USER=wigelldbassa -e MYSQL_PASSWORD=assa mysql:8.0
```
If MySQL workbench server is not installed port can be changed to `3306:3306`

