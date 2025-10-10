# Wigell Gym Service 

---

## Related projects
- **[Gateway]** (https://github.com/SaraSnail/WigellGateway)

---

## 👾 Project uses

- **Language :** Java 21
- **Build :** Apache Maven 
- **Framework :** Spring boot 3.5.5
  - Spring Data JPA
  - Spring Web
- **Database :** MYSQL 8.0 in Docker container
- **Security :** Spring Security
- **Logging :** Log4j2
  - Jansi 2.4.0
- **External API :** Currency API from API plugin - (https://apiplugin.com/)
    - Currency conversion: **SEK** (kr) ⇒ **EURO** (€)


- **Testing :** Spring boot testing
  - Database : H2
  - Mockito


---

### 🔌 Ports
- **Gym Service :** `6565`
- **MySQL :** `3306`
- **Gateway :** `4545`

---

### 🚀 Endpoints

#### Admin:
* **GET** `/api/wigellgym/listcanceled` - Lists all canceled bookings
* **GET** `/api/wigellgym/listupcoming` - Lists all upcoming workouts with active bookings
* **GET** `/api/wigellgym/listpast` - Lists all bookings that weren't cancelled on workouts that has already happened
* **POST** `/api/wigellgym/addworkout/{instructorId}` - Creates a new workout with given instructor
* **PUT** `/api/wigellgym/updateworkout/{workoutId}/{instructorId}` - Updates the filled in fields and can change instructor with _instructorId_
* **PUT** `/api/wigellgym/remworkout/{workoutId}` - Updates workout so it's inactive and can't be booked. Also cancels all bookings if workout has not happened yet
* **POST** `/api/wigellgym/addinstructor` - Creates a new instructor
* **GET** `/api/wigellgym/instructors` - Gets all instructors

#### User:
* **GET** `/api/wigellgym/workouts` - Lists all active, future and not fully booked workouts
* **GET**` /api/wigellgym/mybookings` - Get the users bookings, both active and inactive
* **POST** `/api/wigellgym/bookworkout/{workoutId}` - Books workout on the user
* **PUT** `/api/wigellgym/cancelworkout/{bookingId}` - Cancels the booking on the workout. Gets stopped if ex workout is less than 24 hours away
* **GET** `/api/wigellgym/instructors` - Gets all active instructors

---

### 🐋 Docker
 - The Dockerfile controls the timezone on the container. It's set to Europe Stockholm right now.
 - To easily make a container of the project run the file "[script.bat](script.bat)"
 - Network name is "wigell-network"
