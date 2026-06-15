# Gold Standard Assurance Platform Tech Stack Map

This document maps the Gold Standard Assurance Platform stack to concepts that
are familiar from Python and FastAPI projects. It is intended for engineers who
already understand full-stack web development, HTTP APIs, background jobs,
database migrations, CI/CD, and cloud deployment, but are newer to Java and
Spring Boot.

The short version: the backend is a Java 21 Spring Boot API with a layered
controller/service/repository architecture, Maven builds, Liquibase-managed
database migrations, Spring Security JWT auth, JPA/Hibernate persistence, and
GitLab CI deploying container images to AWS ECS.

## Repository Map

| Path | What It Is | FastAPI-Oriented Mental Model |
| --- | --- | --- |
| `back-end/` | Current Java Spring Boot API | Your main FastAPI backend service |
| `back-end-spring-boot-4-upgrade/` | Parallel Spring Boot 4 migration copy | A branch/worktree testing a major framework upgrade |
| `front-end/` | React/Vite/TypeScript UI | A regular React or Next.js client app |
| `automation/` | Cypress end-to-end tests | Playwright/Cypress browser tests around the deployed app |
| `load-test/` | Locust load testing project | Python load-test harness calling your API |
| `infrastructure/` | Terraform for AWS resources | IaC for VPC, ECS, RDS, Cognito, S3, SES, WAF |
| `images/` | Docker base and pipeline images | Shared CI/runtime image definitions |
| `docs/` | Documentation | Project docs |

## Backend Stack At A Glance

| Area | This Project | FastAPI Equivalent |
| --- | --- | --- |
| Language/runtime | Java 21 | Python 3.11 or 3.12 |
| Web framework | Spring Boot 3.5.4 | FastAPI |
| Build/dependency tool | Maven | `uv`, Poetry, Hatch, or pip-tools |
| Build manifest | `pom.xml` | `pyproject.toml` |
| App entry point | `AssuranceApplication` with `@SpringBootApplication` | `main.py` with `app = FastAPI()` |
| HTTP route layer | `@RestController` classes | `APIRouter` modules |
| Business logic | `@Service` classes | Service modules/classes injected with `Depends` |
| Persistence | Spring Data JPA / Hibernate | SQLAlchemy / SQLModel |
| ORM entities | `@Entity` classes | SQLAlchemy model classes |
| Repository layer | Spring Data `Repository` interfaces | DAO/repository classes or SQLAlchemy query helpers |
| Validation | Bean Validation annotations and custom validators | Pydantic models and validators |
| Auth | Spring Security OAuth2 resource server | FastAPI security dependencies with JWT validation |
| Config | `application.yml` and profile YAML files | Pydantic Settings plus `.env` files |
| Migrations | Liquibase changelogs in sibling `data-migration` repo | Alembic migrations |
| Unit tests | JUnit via Maven Surefire | pytest unit tests |
| Integration tests | JUnit via Maven Failsafe | pytest integration tests, often with test DB/client |
| Coverage | JaCoCo | coverage.py / pytest-cov |
| Formatting | Spotless + Google Java Format | Ruff/Black |
| Static analysis | SonarQube | SonarQube, Ruff, mypy, Bandit depending on setup |
| API docs | Springdoc OpenAPI | FastAPI automatic OpenAPI |
| Packaging | Spring Boot executable jar | Docker image with Python app server |
| Runtime server | Embedded servlet server from Spring Boot | Uvicorn/Gunicorn/Uvicorn workers |

## Maven Is The Backend Build System

The main Java build file is `back-end/pom.xml`.

In a Python service, `pyproject.toml` usually declares dependencies, package
metadata, tool config, and sometimes build backend behavior. In this Java
service, `pom.xml` does that job.

Important project coordinates:

| Maven Field | Value | Python Analogy |
| --- | --- | --- |
| `groupId` | `org.goldstandard` | Package namespace or organization |
| `artifactId` | `assurance-api` | Package/project name |
| `version` | `0.0.1-SNAPSHOT` | App/package version |
| parent | `spring-boot-starter-parent:3.5.4` | Framework-managed dependency baseline |
| `java.version` | `21` | Required Python runtime version |

There is no Maven wrapper in this repo, so developers need Maven installed
locally. CI uses Maven through container images.

Local Java version is guided by `back-end/.sdkmanrc`:

```text
java=21.0.8-librca
```

That is roughly like having a `.python-version` or `.tool-versions` file for
Python.

## Spring Boot Starters Are Dependency Bundles

Spring Boot starters are curated dependency bundles. They feel similar to
installing FastAPI plus common companion packages in one go.

| Spring Dependency | Purpose | FastAPI Mental Model |
| --- | --- | --- |
| `spring-boot-starter-web` | HTTP API framework with embedded server | `fastapi`, `starlette`, `uvicorn` |
| `spring-boot-starter-security` | AuthN/AuthZ framework | FastAPI auth dependencies/middleware |
| `spring-boot-starter-oauth2-resource-server` | JWT/OIDC resource server support | JWT verifier against Cognito/OIDC issuer |
| `spring-boot-starter-data-jpa` | ORM and database integration | SQLAlchemy/SQLModel integration |
| `spring-boot-starter-data-rest` | Data REST support | Generic resource route helpers, if used |
| `spring-boot-starter-validation` | Bean validation | Pydantic validation |
| `spring-boot-starter-actuator` | Health/info endpoints | `/health`, metrics, app info endpoints |
| `spring-boot-starter-cache` | Cache abstraction | `cachetools`, Redis wrappers, or custom cache layer |
| `spring-boot-starter-freemarker` | Server-side templating | Jinja2 |
| `spring-boot-starter-test` | Test stack | pytest plus common fixtures/assertion libs |

The parent Spring Boot version also manages many transitive dependency versions,
which is why not every dependency in `pom.xml` declares its own version.

## Application Structure

The backend package root is:

```text
back-end/src/main/java/org/goldstandard/assurance/api
```

The code follows a common layered service pattern:

| Java Package | Role | FastAPI Equivalent |
| --- | --- | --- |
| `controller` | HTTP endpoints | `routers/` |
| `service` | Business logic and orchestration | `services/` |
| `repository` | Database query access | `repositories/` or DAO layer |
| `entity` | ORM database models | `models/db.py` SQLAlchemy models |
| `dto` | API/request/response data transfer objects | Pydantic schemas |
| `configuration` | Beans, clients, settings, security config | dependency wiring, settings, app startup config |
| `converter` | Mapping between entities and DTOs | schema/model mappers |
| `validation` | Custom validation annotations/validators | custom Pydantic validators |
| `exception` | App exception types and error handling | exception classes and FastAPI exception handlers |
| `projection` | Partial database projections/views | selected columns, view models, DTO projections |

Approximate main Java file distribution:

| Area | File Count |
| --- | ---: |
| `service` | 465 |
| `entity` | 167 |
| `dto` | 161 |
| `repository` | 96 |
| `controller` | 50 |
| `configuration` | 42 |
| `converter` | 40 |
| `exception` | 30 |

That distribution says this is service-heavy backend code: most behavior lives
below the HTTP route layer, not inside controllers.

## Controllers Map To FastAPI Routers

Spring controllers are classes annotated with `@RestController`. Methods inside
those classes are bound to HTTP routes with annotations such as `@GetMapping`,
`@PostMapping`, `@PutMapping`, and `@DeleteMapping`.

FastAPI mental model:

```python
router = APIRouter()

@router.post("/projects")
def create_project(payload: ProjectCreate, service: ProjectService = Depends()):
    return service.create_project(payload)
```

Spring mental model:

```java
@RestController
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/projects")
    public ProjectResponse createProject(@RequestBody ProjectRequest request) {
        return projectService.createProject(request);
    }
}
```

The exact syntax is different, but the transfer is direct:

| FastAPI Concept | Spring Boot Concept |
| --- | --- |
| `APIRouter` | `@RestController` class |
| route decorator | `@GetMapping`, `@PostMapping`, etc. |
| Pydantic request model | DTO with validation annotations |
| dependency via `Depends` | constructor-injected Spring bean |
| response model | DTO returned from controller |
| exception handler | `@ControllerAdvice` |

## Services Map To Business Logic Dependencies

Spring `@Service` classes are where business logic usually lives. They are
automatically registered in the application container and injected into other
classes through constructors.

This is conceptually similar to using FastAPI dependencies for service objects,
except the Spring container handles object lifecycle and dependency wiring for
the whole app by default.

| FastAPI Pattern | Spring Pattern |
| --- | --- |
| `Depends(get_project_service)` | constructor injection of `ProjectService` |
| service class or module | `@Service` class |
| explicit dependency factory | Spring bean registration/component scanning |
| per-request dependency scope | usually singleton beans unless configured otherwise |

Most Spring services are singleton objects. They should generally be stateless
or carefully manage state. Think of them as long-lived service objects rather
than per-request Python objects.

## Repositories And Entities Map To SQLAlchemy

The persistence stack is Spring Data JPA plus Hibernate.

| This Project | FastAPI Equivalent |
| --- | --- |
| `@Entity` | SQLAlchemy ORM model |
| `@Repository` | DAO/repository class |
| Spring Data repository methods | SQLAlchemy query methods |
| Hibernate | SQLAlchemy ORM internals |
| JPA relationships | SQLAlchemy relationships |
| H2 test database | SQLite/test Postgres-style test DB, though here H2 emulates PostgreSQL |
| PostgreSQL runtime database | PostgreSQL runtime database |

Spring Data repositories often define interfaces rather than concrete classes.
Spring generates implementation behavior based on method names and annotations.
That is one of the biggest differences from typical Python code, where query
functions are usually explicit.

Example mental translation:

| Spring Data Style | Python Style |
| --- | --- |
| `findByProjectIdAndStatus(...)` | `select(...).where(Model.project_id == ..., Model.status == ...)` |
| interface extends repository | class/function wrapping SQLAlchemy session |
| generated query from method name | explicit query expression |

## Liquibase Is The Alembic Equivalent

Database migration is handled by Liquibase.

The local compose file starts a `liquibase/liquibase` container and points it at
a sibling repository path:

```text
../../data-migration/migrations
```

with:

```text
--changeLogFile=changelog.yaml
--contexts="local" update
```

So the conceptual mapping is:

| Python/FastAPI | This Project |
| --- | --- |
| Alembic | Liquibase |
| `versions/*.py` migration files | Liquibase changelog YAML/XML/SQL files |
| `alembic upgrade head` | `liquibase update` |
| env-specific migration config | Liquibase contexts such as `local` |

The migration definitions are not primarily inside `back-end/`; they are
expected from the sibling `data-migration` repo.

## Configuration And Profiles

Spring configuration lives in YAML files under:

```text
back-end/src/main/resources
```

Important files:

| File | Purpose | FastAPI Equivalent |
| --- | --- | --- |
| `application.yml` | Default app config | base Pydantic Settings defaults |
| `application-local.yml` | Local developer profile | `.env.local` / local settings |
| `application-test.yml` | Test profile with H2 DB and local JWT key | pytest settings |
| `application-production.yml` | Production overrides | production settings |

Spring profiles are roughly environment-specific settings layers. Running with:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

is similar to starting a FastAPI app with:

```bash
APP_ENV=local uvicorn app.main:app
```

The default API context path is:

```text
/api
```

so routes are served under paths such as `/api/projects`.

## Authentication And Authorization

The backend uses Spring Security and configures itself as an OAuth2 resource
server that expects JWTs from an OIDC provider. The README and configuration
refer to AWS Cognito as the identity provider.

FastAPI mental model:

| FastAPI | Spring Boot |
| --- | --- |
| OAuth2/JWT dependency validates bearer token | Spring Security resource server validates bearer token |
| route dependencies enforce permissions | annotations/services enforce role and permission checks |
| JWKS/OIDC issuer config | `spring.security.oauth2.resourceserver.jwt.issuer-uri` |
| custom permission service | `PermissionsService` and controller/service checks |

For local testing, the app can run with a local key pair and a `/token` endpoint
under the test profile. That is similar to test-only auth token generation in a
FastAPI app.

## Background Jobs And Scheduled Work

The app enables scheduling at startup. Scheduled jobs are declared with
`@Scheduled`.

Examples visible from the code/config include:

| Job Type | Purpose | FastAPI Equivalent |
| --- | --- | --- |
| user sync | periodically sync users from Cognito | APScheduler/Celery/RQ/cron worker |
| Comprehend polling | poll AWS Comprehend job completion | background worker/queue polling |
| registry updates | push/poll external registry updates | periodic task |
| SDG tool updates | push/poll external SDG updates | periodic task |
| conversation notifications | send scheduled notifications | scheduled task |
| stakeholder consultation notifications | send daily notifications | scheduled task |

Spring scheduled jobs run inside the application process unless separated out.
That is different from many FastAPI deployments where scheduled/background work
often lives in a Celery/RQ/Arq worker process.

## External Integrations

The backend integrates with several external systems and cloud services.

| Integration | Java Library/Config | FastAPI Equivalent |
| --- | --- | --- |
| AWS Cognito | AWS SDK `cognitoidentityprovider` | boto3 Cognito client |
| AWS S3 | AWS SDK `s3` | boto3 S3 client |
| AWS SES | AWS SDK `sesv2` | boto3 SES client |
| AWS Comprehend | AWS SDK `comprehend` | boto3 Comprehend client |
| AWS Textract | AWS SDK `textract` | boto3 Textract client |
| AWS SNS logging | AWS SDK `sns` | boto3 SNS client |
| Azure Blob Storage | Azure Storage Blob SDK | Azure Python SDK blob client |
| Azure Identity | Azure Identity SDK | Azure Identity Python SDK |
| LibreOffice conversion | JODConverter | unoconv/LibreOffice subprocess or document-conversion service |
| OpenAPI | Springdoc | FastAPI built-in OpenAPI |

## Testing Model

Tests live under:

```text
back-end/src/test/java
```

The Maven plugins split tests by naming convention:

| Test Type | Java/Maven Rule | FastAPI Equivalent |
| --- | --- | --- |
| Unit tests | Surefire runs tests except `*IntegrationTest.java` | `pytest tests/unit` |
| Integration tests | Failsafe runs `*IntegrationTest.java` | `pytest tests/integration` |
| Coverage | JaCoCo merges unit and integration coverage | pytest-cov/coverage.py |
| Test DB | H2 in PostgreSQL compatibility mode | ephemeral DB/test container/SQLite substitute |
| Security tests | Spring Security Test | FastAPI test client with auth fixtures |
| HTTP integration tests | Spring app starts on random port | `TestClient` or async HTTP client against app |

Common commands:

```bash
mvn test
mvn verify -Dskip.unit.tests
mvn verify -Dskip.unit.tests -Dskip.integration.tests
```

The last command is used by CI to merge/report existing coverage artifacts
without rerunning unit or integration tests.

## Formatting, Linting, And Quality Gates

| Concern | This Project | Python Equivalent |
| --- | --- | --- |
| Java formatting | Spotless + Google Java Format | Black/Ruff format |
| Unused imports | Spotless removes unused imports | Ruff |
| Coverage | JaCoCo | coverage.py / pytest-cov |
| Static analysis | SonarQube | SonarQube, Ruff, mypy, Bandit |
| License checks | Maven license plugin | pip-licenses / licensecheck |
| Dependency scan | OWASP dependency-check | pip-audit / Safety / osv-scanner |

Useful commands:

```bash
mvn spotless:apply
mvn clean compile
mvn clean package
```

Note that Spotless is bound to the Maven lifecycle at `generate-sources`, so a
normal Maven build can fail on formatting.

## CI/CD

The backend GitLab pipeline has these stages:

```text
build -> dependency-check -> test -> static-analysis -> deploy
```

Backend job mapping:

| GitLab Job | Command | FastAPI Equivalent |
| --- | --- | --- |
| `build` | `mvn clean package` | build/install package, maybe Docker build |
| `license-check` | Maven license plugin | license check for Python deps |
| `unit-test` | `mvn test` | pytest unit tests |
| `integration-test` | `mvn verify -Dskip.unit.tests` | pytest integration tests |
| `coverage-report` | JaCoCo report generation | coverage report generation |
| `sonar` | `gitlab-sonar-scanner` | Sonar scan |
| deploy jobs | build jar, build Docker image, push ECR, redeploy ECS | Docker image deployment |

Deployments build a jar, place it into a Docker image, push to AWS ECR, and for
development force an ECS service deployment.

## Docker And Runtime Packaging

Spring Boot packages the app as an executable jar. The app Dockerfile copies the
jar to `/app.jar` and runs:

```bash
java -jar /app.jar
```

FastAPI mental model:

```bash
uvicorn app.main:app
```

or in production:

```bash
gunicorn -k uvicorn.workers.UvicornWorker app.main:app
```

The runtime base image includes Java 21 plus LibreOffice Writer and fonts. That
extra LibreOffice dependency is there for document conversion through
JODConverter.

## Local Development Flow

Typical backend local setup:

1. Use Java 21, preferably the SDKMAN version in `.sdkmanrc`.
2. Install Maven.
3. Start local PostgreSQL and Liquibase from `back-end/local/compose.yml`.
4. Run the API with the `local` profile when integrating with the frontend.
5. Run the API with the `test` profile when using standalone local test tokens.

Representative commands:

```bash
cd back-end
docker-compose -f local/compose.yml up -d
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

Standalone test profile:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=test"
TOKEN=$(curl -s "http://127.0.0.1:8080/api/token?role=GS-Admin")
```

## Frontend Stack

The frontend is familiar territory if you know React or Next.js, though this is
not a Next.js app. It is a Vite React single-page app with a small Express layer
for local/production-style serving.

| Area | This Project |
| --- | --- |
| UI framework | React 18 |
| Build tool | Vite 7 |
| Language | TypeScript 4.9 |
| UI library | MUI 5 |
| Routing | React Router 7 |
| Forms | Formik, Yup, React JSON Schema Form |
| Tests | Jest 30, Testing Library |
| Lint/style | ESLint 9, Prettier 3 |
| CI Node image | Node 20.19.0 |

FastAPI project analogy: this is the separate frontend package you would keep
next to your API service in a monorepo. It calls the backend under `/api` and
uses Cognito-backed auth flows.

Common commands:

```bash
npm ci
npm run build
npm run test:ci
npm run lint
npm start
npm run start:prod-local
```

## Browser Automation

The `automation/` project is Cypress-based E2E testing.

| Area | This Project | FastAPI/TS Analogy |
| --- | --- | --- |
| Browser test runner | Cypress 13 | Playwright or Cypress |
| BDD syntax | Cypress Cucumber Preprocessor | Behave/Cucumber/Gherkin-style tests |
| Accessibility | axe-core and cypress-axe | axe checks in Playwright/Cypress |
| Test data | fixtures under `cypress/fixtures` | JSON fixtures |
| Page model | `cypress/e2e/pages` | Page object model |

This tests the full web app, not just backend routes.

## Load Testing

The `load-test/` project uses Locust.

| Area | This Project |
| --- | --- |
| Load tool | Locust 2.32.1 |
| Config | `locust.conf` |
| Main file | `locustfile.py` |
| Shape file | `custom_load_shape.py` |
| Client/service helpers | `load-test/src/client`, `load-test/src/service` |

This part is already Python, so it should feel familiar.

## Infrastructure

Infrastructure is Terraform for AWS.

| Module | Purpose |
| --- | --- |
| `cognito_user_pool` | User pool and user groups |
| `ecs` | ECS service, ECR, ALB, S3 document store, WAF, logs |
| `network` | VPC, subnets, internet gateway, NACLs, Route53/ACM pieces |
| `rds` | PostgreSQL RDS and bastion host |
| `ses` | Email sending infrastructure |
| `document_migration` | EC2/security resources for document migration |

The Terraform CI validates, plans, runs security/code analysis, and applies from
GitLab for development, staging, and production. The README explicitly says
`terraform apply` should be run by pipeline, not locally.

## Spring Boot 4 Upgrade Folder

The `back-end-spring-boot-4-upgrade/` directory is a parallel copy of the backend
for a major Spring Boot upgrade.

Key differences from the main backend:

| Area | Main Backend | Upgrade Copy |
| --- | --- | --- |
| Spring Boot | 3.5.4 | 4.0.2 |
| Lombok | 1.18.38 | 1.18.42 |
| AWS SDK BOM | 2.32.21 | 2.41.25 |
| Azure SDK BOM | 1.2.38 | 1.3.4 |
| PostgreSQL JDBC | 42.7.7 | 42.7.9 |
| Springdoc | 2.8.9 | 3.0.1 |
| Jackson | 2.x `com.fasterxml.jackson` | 3.x `tools.jackson` |
| H2 | 2.3.232 | 2.4.240 |

Treat this like a migration branch: useful for understanding future direction,
but do not assume it is the active backend unless a task explicitly points there.

## What Transfers Cleanly From FastAPI

These fundamentals transfer directly:

| Skill You Already Have | How It Applies Here |
| --- | --- |
| HTTP routing | Controllers expose routes just like FastAPI routers |
| Request/response modeling | DTOs fill the role of Pydantic schemas |
| Dependency injection | Spring has a larger DI container, but the idea is familiar |
| Service-layer design | Business logic should stay out of controllers |
| Database modeling | JPA entities map to ORM models |
| Migration discipline | Liquibase fills the Alembic role |
| Auth middleware/dependencies | Spring Security handles the same boundary |
| Background jobs | `@Scheduled` methods fill the periodic-task role |
| Environment config | Spring profiles map to app settings per env |
| CI stages | Build, test, lint, scan, deploy are the same concerns |

## Java/Spring Concepts To Watch For

These are the areas most likely to feel different from FastAPI:

| Concept | Why It Feels Different |
| --- | --- |
| Annotations | Java metadata drives routing, validation, config, ORM, and DI |
| Component scanning | Spring auto-discovers many classes instead of explicit app wiring |
| Generated repository behavior | Spring Data can derive queries from method names |
| Singleton service beans | Services are usually long-lived container-managed objects |
| Transaction boundaries | Often annotation-driven with `@Transactional` |
| Framework lifecycle | Startup/config/bean creation order matters more than in many Python apps |
| Checked build lifecycle | Maven phases and plugin bindings can run formatting, tests, reports, and packaging |
| Profiles | Config overlays are first-class and affect which classes are loaded |

## Practical Orientation Checklist

When you need to understand or change a backend feature:

1. Start at the controller for the route.
2. Follow constructor-injected services.
3. Follow service calls into repositories.
4. Check DTOs for request/response shape.
5. Check entities for persisted shape.
6. Check `application*.yml` for config and profile behavior.
7. Check tests with the same feature name, especially `*IntegrationTest`.
8. If schema changes are needed, look in the sibling Liquibase migration repo.

That is the same mental loop as FastAPI:

```text
router -> service -> repository/session -> model/schema -> settings -> tests -> migration
```

Only the names and framework mechanics are different.
