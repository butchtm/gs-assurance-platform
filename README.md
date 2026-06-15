# GS Assurance Platform Local Setup

This workspace is a combined local export of the Gold Standard Assurance repositories:

- `back-end` - Spring Boot API
- `front-end` - Vite/React UI
- `data-migration` - Liquibase migrations used to create the local database
- `automation`, `images`, `infrastructure`, `load-test` - supporting exports

The previous local export is archived under `archive/`.

## Seed Data

The local database is not seeded with full demo/business data.

Liquibase creates the schema and loads reference/configuration data. In the current local DB, migrations created lookup data such as account types and request types, plus three platform accounts. It did not create sample users, projects, or requests:

```text
account      3
account_type 8
project      0
request      0
request_type 38
user         0
```

To exercise business flows, create data through the UI or restore a sanitized Development/Staging backup if one is provided later.

## Prerequisites

- Docker Desktop or OrbStack running
- Java 21
- Maven
- `nvm`
- Node.js `20.19.0`
- AWS CLI logged in to the account that can access the Development Cognito user pool

Check AWS access:

```bash
aws sts get-caller-identity
```

If the AWS session has expired, log in again before starting the backend.

## Local Files

These local-only files are already created and ignored by git:

- `back-end/.env`
- `front-end/.env.local`

The backend env points at the Development Cognito pool and local Postgres on port `15432`.

The frontend env points Vite's `/api` proxy at `http://localhost:8080`.

## Database and Migrations

Start local Postgres and run Liquibase from the repo root:

```bash
cd /Users/patslaurel/projects/gs-assurance-platform
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml up --build -d
```

This uses `back-end/local/compose.local.yml` to expose Postgres on host port `15432`, avoiding conflicts with other local Postgres containers on `5432`. Liquibase still connects to `db:5432` inside the Docker network.

Check status:

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml ps
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml logs liquibase
```

Verify applied changesets:

```bash
docker exec local-db-1 psql -U postgres -d assurance_db \
  -c 'select count(*) as changelog_rows from public.databasechangelog;'
```

## Backend

Start the Spring Boot API in a new terminal:

```bash
cd /Users/patslaurel/projects/gs-assurance-platform/back-end
set -a
source .env
eval "$(aws configure export-credentials --format env)"
set +a
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

The AWS export command passes short-lived credentials from the active AWS CLI session into the Java process.

If startup fails with `The security token included in the request is expired`,
run your AWS login again and restart the backend from a fresh terminal using the
commands above. The backend syncs users from Cognito during startup, so stale
AWS credentials will stop the app before login can work.

Health check:

```bash
curl http://localhost:8080/api/actuator/health
```

## Login

Login is not seeded from the local database. The app authenticates against the Development AWS Cognito pool:

```text
User pool: eu-central-1_XIzobZSx7
App client: 5vm30ddm7gk24bf4hpcin7p9cq
```

The Development pool already contains test usernames such as `Admin`, `Developer`, `ARMTeam`, `Registry`, `Finance`, `TAC`, and `Support`, but this export does not include their passwords.

If you do not have the shared test password, create your own Cognito user and assign it a group:

```bash
USER_POOL_ID=eu-central-1_XIzobZSx7
USERNAME=pats-admin
EMAIL=patslaurel@thinkingmachin.es
TEMP_PASSWORD='replace-with-a-strong-temp-password'

aws cognito-idp admin-create-user \
  --user-pool-id "$USER_POOL_ID" \
  --username "$USERNAME" \
  --user-attributes \
    Name=email,Value="$EMAIL" \
    Name=email_verified,Value=true \
    Name=given_name,Value=Pats \
    Name=family_name,Value=Laurel \
  --temporary-password "$TEMP_PASSWORD" \
  --region eu-central-1

aws cognito-idp admin-add-user-to-group \
  --user-pool-id "$USER_POOL_ID" \
  --username "$USERNAME" \
  --group-name GS-Admin \
  --region eu-central-1
```

Then log in at `http://localhost:3000` with that username and temporary password. The UI should prompt for a new permanent password.

If you create the user after the backend has already started, restart the backend so its startup user-sync imports the Cognito user into the local `gs.user` table.

## Frontend

Start the Vite frontend in another terminal:

```bash
cd /Users/patslaurel/projects/gs-assurance-platform/front-end
source ~/.nvm/nvm.sh
nvm install 20.19.0
nvm use 20.19.0
npm install --ignore-scripts
npm start
```

Open:

```bash
http://localhost:3000
```

`npm install --ignore-scripts` avoids running `husky install`, which expects the frontend to be a standalone git checkout. In this combined export, `.git` is at the workspace root.

## Useful Commands

Stop local database containers:

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml down
```

Reset the local database and rerun all migrations:

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml down -v
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml up --build -d
```

Re-export AWS credentials after a fresh login:

```bash
eval "$(aws configure export-credentials --format env)"
```

## Notes

- `back-end/local/compose.yml` mounts `../../data-migration/migrations`, which resolves to this repo's `data-migration/migrations` directory.
- `front-end/vite.config.ts` proxies `/api` to `VITE_PROXY_TARGET`, defaulting to `http://localhost:8080`.
- The app uses Development Cognito for local login, so users must exist in that Cognito pool.
