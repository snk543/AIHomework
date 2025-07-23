# JSONPlaceholder API Clone

This project is a Java-based implementation of the JSONPlaceholder API with extended features including JWT authentication, PostgreSQL database, and containerized deployment.

## Features

- Full REST API implementation matching JSONPlaceholder endpoints
- JWT-based authentication
- PostgreSQL database with Liquibase migrations
- Docker and Docker Compose support
- Spring Boot 3.2.3
- Spring Security
- Spring Data JPA
- Liquibase for database migrations

## Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Maven

## Getting Started

1. Clone the repository:
```bash
git clone <repository-url>
cd jsonplaceholder
```

2. Build and run with Docker Compose:
```bash
docker-compose up --build
```

The application will be available at `http://localhost:8080/api`

## API Endpoints

### Users

- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Authentication

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

## Development

### Local Development

1. Start PostgreSQL:
```bash
docker-compose up db
```

2. Run the application:
```bash
./mvnw spring-boot:run
```

### Database Migrations

Database migrations are managed by Liquibase. Migration files are located in `src/main/resources/db/changelog/`.

## Testing

Run tests with:
```bash
./mvnw test
```

## Security

- JWT tokens are used for authentication
- Passwords are hashed using bcrypt
- All endpoints except login and register require authentication

## License

This project is licensed under the MIT License. 