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

### One-time setup

Copy the example config files (both are gitignored once copied):

```bash
cp .env.example .env
cp cvbackend/src/main/resources/application-local.properties.example \
  cvbackend/src/main/resources/application-local.properties
```

Keep database credentials in `.env` and `application-local.properties` in sync.

### Run

Start PostgreSQL:

```bash
docker compose up -d
```

Run backend tests:

```bash
cd cvbackend && ./mvnw test
```

Run the backend (uses the `local` profile by default):

```bash
cd cvbackend && ./mvnw spring-boot:run
```

### Application profiles

| Profile | When it runs | Where credentials come from |
|---------|--------------|-----------------------------|
| `local` | Default for `spring-boot:run` | gitignored `application-local.properties` |
| `test` | `./mvnw test` (`@ActiveProfiles("test")`) | `application-test.properties` |
| `prod` | Set `SPRING_PROFILES_ACTIVE=prod` | Environment variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` |

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

1. Complete the one-time setup above
2. Start PostgreSQL (`docker compose up -d`)
3. Start or test the backend (`./mvnw spring-boot:run` or `./mvnw test`)

Do not change the database schema manually. Add a new Flyway migration instead.
