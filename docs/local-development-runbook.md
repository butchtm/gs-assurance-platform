---
name: local-development-runbook
description: Start the local Assurance Platform stack with the default Cognito login or opt-in fake credentials.
---

# Local Development Runbook

This checkout is arranged as a combined local workspace. The backend compose file mounts the repo-root `data-migration/migrations` directory into Liquibase.

## Prerequisites

- Docker Desktop running
- Java 21
- Maven
- Node.js 20.19.0
- For the default Cognito login only: AWS CLI credentials for the Development Cognito user pool

## Start the local database

From the repository root:

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml up --build -d
```

The local override exposes Postgres on host port `15432` to avoid conflicts with other local Postgres containers. Liquibase still connects to `db:5432` inside the Docker network.

## Choose an authentication mode

### Default: Development Cognito

The plain `local` profile remains the default local workflow. In a backend terminal:

```bash
cd back-end
set -a
source .env
eval "$(aws configure export-credentials --format env)"
set +a
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

The backend syncs Cognito users on startup. If startup fails with `The security token included in the request is expired`, log in to AWS again and restart the backend from a fresh terminal.

### Opt in: fake local credentials

Use this mode when you want to log in as a user already present in the local database without Development Cognito credentials or Cognito user sync. It uses the existing login form and maps each configured fake alias to an active persisted database user. User groups and account associations come from that database user.

1. Ensure the local database already contains each user you plan to map. There is no fixture or user provisioning in this mode. A mapped user must be active and have an identity-provider user ID.

2. From the repository root, create the ignored config directory and `back-end/config/application-local-auth.yml` with one or more aliases. This external config path is git-ignored; do not commit credentials.

   ```bash
   mkdir -p back-end/config
   ```

   ```yaml
   local-auth:
     credentials:
       developer:
         password: "replace-with-a-local-only-password"
         database-user-name: "existing-database-user-name"
   ```

   `developer` is the alias entered as the login username. Replace both quoted placeholders with local-only values. Add another entry under `credentials` for each additional login alias.

3. In the same backend terminal, set a private local JWT secret of at least 32 characters and start both profiles:

   ```bash
   cd back-end
   export LOCAL_AUTH_JWT_SECRET='replace-with-at-least-32-private-local-characters'
   mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local,local-auth"
   ```

   The placeholder is not a usable secret; replace it with a private value. AWS credentials and Cognito user sync are not needed to start this auth mode.

4. Start the frontend using the common steps below. On the login screen, enter the configured alias and its fake password.

Local-auth issues JWTs that expire after one hour and do not issue a refresh token. When a token expires, the backend clears the auth cookies and the user must log in again. Logout also clears the auth cookies. On HTTP requests to `localhost`, `127.0.0.1`, or `::1` in this two-profile mode, auth cookies are not marked `Secure` so the local browser can send them; they remain `HttpOnly`.

Forgot-password and MFA actions still use Cognito and are not supported for fake users.

## Start the frontend

In a frontend terminal:

```bash
cd front-end
source ~/.nvm/nvm.sh
nvm install 20.19.0
nvm use 20.19.0
npm install --ignore-scripts
npm start
```

`--ignore-scripts` avoids running `husky install`, which expects the frontend to be a standalone git checkout. In this combined export, `.git` is at the workspace root instead.

Open `http://localhost:3000`.

## Verify the local stack

Backend health:

```bash
curl http://localhost:8080/api/actuator/health
```

Database and Liquibase logs:

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml ps
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml logs liquibase
```

For local-auth, also sign in through the frontend with one configured alias. A successful login should return the mapped database user's identity and permissions.

## Troubleshooting

- **Local-auth startup reports a short or missing `LOCAL_AUTH_JWT_SECRET`:** set a non-empty value of at least 32 characters in the backend terminal, then restart.
- **Local-auth startup reports no credentials or incomplete credential configuration:** check that `back-end/config/application-local-auth.yml` exists and each entry has an alias, `password`, and `database-user-name`.
- **Fake login fails:** confirm the entered username is the configured alias and that the mapped database username identifies an active user with an identity-provider user ID.
- **Login works but the account or groups are unexpected:** update the mapped user's persisted database record; roles and account access are not supplied by the fake credential mapping.
- **Login expires after an hour:** this mode has no refresh-token flow; sign in again.

## References

- Backend local and local-auth profile configuration: `back-end/src/main/resources/application-local.yml` and `back-end/src/main/resources/application-local-auth.yml`
- Local fake login configuration: `back-end/config/application-local-auth.yml` (developer-local, git-ignored)
