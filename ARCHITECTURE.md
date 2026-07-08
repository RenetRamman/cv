# Architecture

## Project Overview

This project is a dynamic CV and portfolio website. The goal is to present CV information, projects, and skills, while allowing the content to be managed through an admin interface.

Unlike a static CV page, the content is stored in a PostgreSQL database and served through a Spring Boot backend API. This makes the project useful as both a portfolio and a demonstration of backend development, database design, authentication, deployment, and maintainable software architecture.

## Goals

- Build a database-driven CV and portfolio website.
- Demonstrate backend development with Java and Spring Boot.
- Use PostgreSQL as the main relational database.
- Manage database schema changes with Flyway.
- Provide a REST API for frontend and admin functionality.
- Support Estonian and English localization.
- Add authentication for admin-only content management.
- Deploy the application to a real server environment.
- Keep the project clean, maintainable, and suitable for long-term extension.

## Technology Choices

### Java 21

Java 21 was chosen because it is a modern Long-Term Support version of Java and is widely relevant in professional backend development.

### Spring Boot 4

Spring Boot was chosen because it is one of the most commonly used Java backend frameworks. It provides strong support for REST APIs, database access, validation, security, and production-ready application development.

### PostgreSQL

PostgreSQL was chosen as the main database because it is reliable, widely used in industry, and well suited for structured CV, project, and user data.

### Flyway

Flyway is used for database migrations. This allows database schema changes to be versioned, repeatable, and tracked in Git instead of being applied manually.

### React and TypeScript

React with TypeScript is planned for the frontend because it is widely used in modern web development and frequently requested in full-stack and frontend job postings.

## Planned High-Level Architecture

```text
Browser
  |
  v
React Frontend
  |
  v
Spring Boot REST API
  |
  v
PostgreSQL Database
