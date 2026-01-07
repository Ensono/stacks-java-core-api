# Stacks Core API - AI Coding Instructions

## Architecture & Patterns

### 1. Interface-Driven Controllers

Controllers MUST follow the interface-first pattern. Define endpoints, mappings, and OpenAPI documentation in an interface, then implement it in a separate class.

- **Interface**: `com.amido.stacks.core.api.auth.AuthController` (Annotated with `@RequestMapping`, `@Operation`)
- **Implementation**: `com.amido.stacks.core.api.auth.impl.AuthControllerImpl` (Annotated with `@RestController`)

### 2. Composable API Responses

Use custom annotations to document common API responses instead of repeating `@ApiResponse` blocks.

- Example: Use `@CreateAPIResponses` for standard 201, 400, 401, 403, 409 responses.
- See: `com.amido.stacks.core.api.annotations.*`

### 3. Error Handling & DTOs

- All error responses MUST return the `ErrorResponse` DTO.
- Exceptions should be handled in `ApiExceptionAdvice` or specific implementations.
- DTOs are bifurcated into `com.amido.stacks.core.api.dto.request` and `com.amido.stacks.core.api.dto.response`.
- DTOs MUST use Lombok annotations (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`) to reduce boilerplate.

### 4. Correlation Tracking

The `CorrelationIdFilter` manages request tracking:

- **MDC Key**: `CorrelationId` (default)
- **Request Attribute**: `CorrelationId`
- **Response Header**: `x-correlation-id` (default)
- Always retrieve the correlation ID from the request attribute in controllers or advice: `request.getAttribute("CorrelationId", SCOPE_REQUEST)`.

## Developer Workflow

### Build & Install

Use the Maven wrapper from the `java/` directory:

```bash
./mvnw clean install -Dgpg.skip
```

_Note: `-Dgpg.skip` is required unless you have a local GPG key configured._

### Configuration

Endpoints and settings are managed via Spring properties:

- `${auth.token.url}`: Endpoint mapping for `AuthController`.
- `${auth.resource.url}`: Backend resource URL for token exchange.

## Standards

- **Java Version**: 17 (Targeted in `pom.xml`)
- **Package Base**: `com.amido.stacks.core.api`
- **Documentation**: All public API methods MUST use `io.swagger.v3.oas.annotations` for documentation.

## Security & Compliance

All development MUST adhere to the [Security and Compliance Instructions](./copilot-security-instructions.md). Key requirements include:

- **GPG Signing**: Never bypass GPG signing requirements.
- **Branch Protection**: Use feature branches and PRs for all changes.
- **Least Privilege**: Maintain strict authentication and authorization controls.
- **Standards**: Follow OWASP Top 10, ISO 27001, and other applicable security standards.
