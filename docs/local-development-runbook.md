# Local Development Runbook

This checkout is arranged as a combined local workspace. The backend compose file mounts the repo-root `data-migration/migrations` directory into Liquibase.

## Prerequisites

- Docker Desktop running
- Java 21
- Maven
- Node.js 20.19.0
- AWS CLI logged in to the account that can read the Development Cognito user pool

## Run

From the repository root:

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml up --build -d
```

The local override exposes Postgres on host port `15432` to avoid conflicts with
other local Postgres containers. Liquibase still connects to `db:5432` inside the
Docker network.

In a backend terminal:

```bash
cd back-end
set -a
source .env
eval "$(aws configure export-credentials --format env)"
set +a
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

If startup fails with `The security token included in the request is expired`,
run AWS login again and restart the backend from a fresh terminal. The backend
syncs Cognito users on startup, so stale AWS credentials will stop the app before
login can work.

In a frontend terminal:

```bash
cd front-end
source ~/.nvm/nvm.sh
nvm install 20.19.0
nvm use 20.19.0
npm install --ignore-scripts
npm start
```

`--ignore-scripts` avoids running `husky install`, which expects the frontend to
be a standalone git checkout. In this combined export, `.git` is at the workspace
root instead.

Open `http://localhost:3000`.

## Checks

Backend health:

```bash
curl http://localhost:8080/api/actuator/health
```

Database and Liquibase logs:

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml ps
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml logs liquibase
```
