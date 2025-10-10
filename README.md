# Wigell Gym Service 
[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
## 🏋️ Overview:
Wigell Gym Service is a microservice for managing gym bookings, workouts, and instructors, designed to integrate with the WigellGateway. 
It enables admins to manage workouts and instructors, and users to book, view, and cancel bookings on workouts.
Currency conversion (SEK to EURO) is supported via an external API.
---

## 🧩 Related projects
- [WigellGateway](https://github.com/SaraSnail/WigellGateway) - Main entry point and API Gateway for this microservice.

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

- Java 21
- Maven
- Docker (for running MySQL and containerizing the service)
---

### 🔌 Ports
- **Gym Service :** `6565`
- **MySQL :** `3306`
- **Gateway :** `4545`

---

## 🔒 Authentication & Roles

This service uses **Spring Security** for authentication and authorization.

- **User Roles:**
    - **Admin:** Can manage workouts, instructors, and view all bookings.
    - **User:** Can view, book, and cancel their own workouts.

> _Note: These are not "real" users/admin. They are placeholders for production and used under development._
> 
| Role    | Username | Password |
|---------|:--------:|:--------:|
| ADMIN   |  simon   |  simon   |
| USER    |   alex   |   alex   |
| USER    |   sara   |   sara   |
| USER    |  amanda  |  amanda  |

> _Note: Unauthenticated requests will receive a `401 Unauthorized` response._

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

## 🐳 Docker
- The service is containerized with a custom timezone (Europe/Stockholm).
- Use `script.bat` to build and run the container easily.
- Docker network: `wigell-network`
---

## 🛢️ MySQL Database

| Name     |  Username    | Password |
|----------|:------------:|:--------:|
| wigelldb | wigelldbassa |   assa   |

- **Version:** 8.0 (runs in a Docker container)
- **Default Port:** `3306`
    - In this example port is `3307:3306`. If MySQL workbench server is not installed the port can be changed to `3306:3306`
- **Configuration:** Database credentials (username, password, host, port) are set via environment variables in your `application.properties` or Docker Compose file.
- **Initial Setup Example:**
  ```bash
  docker run -d -p 3307:3306 --name wigell-mysql-service --network wigell-network -e MYSQL_ROOT_PASSWORD=assa -e MYSQL_DATABASE=wigelldb -e MYSQL_USER=wigelldbassa -e MYSQL_PASSWORD=assa mysql:8.0
  ```
  This creates a database named `wigelldb` with the root password `assa`. 
  The containers name becomes `wigell-mysql-service` and it connects with the network `wigell-network`


- **Connection Example:**
  ```
  spring.datasource.url=jdbc:mysql://localhost:3306/wigelldb
  spring.datasource.username=wigelldbassa
  spring.datasource.password=assa
  ```

> _Tip: You can change the database name, username, and password as needed—just update the environment variables and your application config._





