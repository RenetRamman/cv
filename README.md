# Dynamic CV project

Purpose
-------
A personal portfolio and CV website where all content is stored in a PostgreSQL database and managed through an admin interface.

Goals
-----
- Demonstrate backend development
- Demonstrate frontend development
- Demonstrate database design
- Demonstrate authentication
- Demonstrate deployment
- Demonstrate CI/CD
- Demonstrate localization
- Demonstrate testing




# Start PostgreSQL
docker compose up -d

# Run tests (requires Docker)
cd cvbackend && ./mvnw test

# Or start the backend against the running database
cd cvbackend && ./mvnw spring-boot:run
