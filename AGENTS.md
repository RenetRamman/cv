# Project

This is a portfolio CV application that provides a public interactive CV,
role-aware content relevance, printable/PDF CV generation, and an admin
interface for maintaining CV data.

## Technology stack

### Backend
- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Flyway
- REST APIs

### Frontend
- React
- TypeScript

## Development principles

- Prefer simple, maintainable implementations over unnecessary abstraction.
- Follow existing project architecture and naming conventions.
- Do not introduce new dependencies without explaining why they are needed.
- Database schema changes must use Flyway migrations.
- Keep controllers thin and business logic in services.
- Add appropriate automated tests for new behaviour.
- Do not modify unrelated code.
- Do not invent requirements that are not present in the issue or existing code.
- Explain significant architectural decisions.

## CV relevance architecture

- CV content is associated with semantic tags.
- Role profiles assign weights to tags.
- A hidden General profile provides default CV relevance and ordering.
- Permanent role profiles include roles such as Java, Backend, Full Stack,
  and ML/AI.
- Vacancy-specific profiles use the same weighted-tag model.
- All profile types use the same deterministic relevance engine.
- AI may suggest tags and interpret vacancies.
- AI must not invent CV experience, skills, or other unsupported claims.
- AI must not silently modify permanent role-profile weights.

## AI-assisted development

AI should assist development but not replace understanding.

For unfamiliar concepts or significant architectural changes:
1. Explain the proposed approach.
2. Identify the files/components that need modification.
3. Wait for approval before making substantial changes.

Prefer implementing one GitHub issue at a time.

## GitHub issues

Development work is organized using GitHub Issues.

When the user references an issue number:
- Use the GitHub CLI (`gh`) to read the issue details.
- Treat the issue as the primary source of requirements for that task.
- Inspect the existing code before proposing implementation changes.
- Do not implement requirements that are not present in the issue without discussing them first.
- Work on one issue at a time unless explicitly instructed otherwise.

## Sources of requirements

For implementation work:

1. The current GitHub issue defines the requirements for the task.
2. `AGENTS.md` defines project-wide engineering and architectural constraints.
3. `ARCHITECTURE.md` documents broader architectural decisions.
4. `README.md` describes the project's purpose and usage.
5. Existing code and configuration represent the current implementation state.

If requirements conflict or are ambiguous, ask before implementing.
