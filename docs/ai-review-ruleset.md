# AI Code Review Ruleset

These rules will be used by Gemini to review pull requests.

## General
- [ ] Code must be clean, readable, and follow SOLID principles.
- [ ] Variable and function names should be descriptive (e.g., camelCase for Java/JS).
- [ ] No hardcoded secrets or API keys.
- [ ] Ensure proper error handling (try-catch blocks where appropriate).

## Java / Spring Boot
- [ ] Use DTOs for API requests/responses, avoid returning Entities directly.
- [ ] Service layer should handle business logic, Controller should handle HTTP requests.
- [ ] Check for proper `@Transactional` usage.
- [ ] Avoid N+1 query problems in JPA/Hibernate.

## Security
- [ ] Validate inputs in Controllers (`@Valid`, `@NotNull`).
- [ ] Check for potential SQL injection or XSS vulnerabilities.
