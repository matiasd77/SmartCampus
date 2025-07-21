# SmartCampus - University Management System

A modern, modular university management system designed to streamline academic and administrative operations for Albanian universities. Built with Java (Spring Boot) and MySQL, it offers secure, scalable, and user-friendly solutions for students, professors, and staff.

## 🏗️ Architecture

This project follows **Clean Architecture** principles with the following package structure:

```
src/main/java/com/smartcampus/
├── config/           # Configuration classes (Security, JWT, etc.)
├── controller/       # REST controllers for API endpoints
├── dto/             # Data Transfer Objects for request/response
├── entity/          # JPA entities (User, Student, Professor, etc.)
├── exception/       # Global exception handling
├── mapper/          # Object mappers (DTO ↔ Entity)
├── repository/      # Spring Data JPA repositories
└── service/         # Business logic interfaces and implementations
```

## 🚀 Features

- **User Management**: Registration and authentication system
- **Role-Based Access**: STUDENT, PROFESSOR, ADMIN roles
- **Security**: JWT-based authentication with Spring Security
- **Validation**: Comprehensive input validation using Jakarta Validation
- **Database**: JPA/Hibernate with MySQL (H2 for development)
- **API Documentation**: RESTful API design

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.5.3
- **Security**: Spring Security + JWT
- **Database**: MySQL (Production) / H2 (Development)
- **ORM**: Spring Data JPA / Hibernate
- **Validation**: Jakarta Validation
- **Utilities**: Lombok, Maven

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+ (for production)
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd SmartCampus
```

### 2. Configure Database
For development, the application uses H2 in-memory database by default.

For production, update `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smartcampus
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access H2 Console (Development)
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: (leave empty)
- Password: (leave empty)

## 📚 API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get JWT token

### Example Register Request
```json
POST /api/auth/register
{
  "name": "John Doe",
  "email": "john.doe@university.edu",
  "password": "password123",
  "role": "STUDENT"
}
```

### Example Register Response
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@university.edu",
    "role": "STUDENT",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

### Example Login Request
```json
POST /api/auth/login
{
  "email": "john.doe@university.edu",
  "password": "password123"
}
```

### Example Login Response
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "type": "Bearer",
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@university.edu",
    "role": "STUDENT"
  }
}
```

### Protected Endpoints
All endpoints except `/api/auth/**` require authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

#### Role-Based Access
- `/api/admin/**` - Requires ADMIN role
- `/api/professor/**` - Requires PROFESSOR or ADMIN role
- `/api/student/**` - Requires STUDENT, PROFESSOR, or ADMIN role

## 🏛️ Entity Structure

### Current Entities
- **User**: Base user entity with authentication details
- **Role**: Enum for user roles (STUDENT, PROFESSOR, ADMIN)

### Planned Entities
- Student, Professor, Course, Enrollment, Grade, Schedule, Exam, Attendance, Announcement, Notification

## 🔧 Development

### Project Structure
- **config/**: Security configuration, JWT setup
- **entity/**: JPA entities with validation annotations
- **repository/**: Spring Data JPA repositories
- **service/**: Business logic layer
- **controller/**: REST API endpoints
- **dto/**: Data transfer objects
- **mapper/**: Object mapping utilities
- **exception/**: Global exception handling

### Code Style
- Use Lombok for boilerplate code reduction
- Follow Spring Boot best practices
- Implement proper validation
- Use meaningful naming conventions
- Add comprehensive documentation

## 🔒 Security

- JWT-based authentication
- Password encryption with BCrypt
- Role-based access control
- Input validation and sanitization
- CORS configuration

## 📝 TODO

- [ ] Implement JWT authentication
- [ ] Add remaining entities (Student, Professor, Course, etc.)
- [ ] Implement CRUD operations for all entities
- [ ] Add comprehensive unit tests
- [ ] Implement logging and monitoring
- [ ] Add API documentation with Swagger
- [ ] Implement file upload functionality
- [ ] Add email notifications
- [ ] Implement caching
- [ ] Add database migrations

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 📞 Support

For support and questions, please contact the development team. 