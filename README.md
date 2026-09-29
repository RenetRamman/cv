# Dynamic CV project

## Purpose

A personal portfolio and CV website where all content is stored in a PostgreSQL database and managed through an admin interface.

## Goals

- Demonstrate backend development
- Demonstrate frontend development
- Demonstrate database design
- Demonstrate authentication
- Demonstrate deployment
- Demonstrate CI/CD
- Demonstrate localization
- Demonstrate testing

## Local development

Start PostgreSQL:

```bash
docker compose up -d
```

Run backend tests:

```bash
cd cvbackend && ./mvnw test
```

Run the backend:

```bash
cd cvbackend && ./mvnw spring-boot:run
```

## Database migrations

Schema changes are managed with Flyway. Migration scripts live in `cvbackend/src/main/resources/db/migration`.

Naming convention:

```text
V{version}__{description}.sql
```

Example:

```text
V2__create_profile_table.sql
```

Migrations run automatically when the Spring Boot application starts. To apply the latest schema locally:

1. Start PostgreSQL (`docker compose up -d`)
2. Start or test the backend (`./mvnw spring-boot:run` or `./mvnw test`)

Do not change the database schema manually. Add a new Flyway migration instead.
