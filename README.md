# GS Assurance Platform Local Setup

This workspace is a combined local export of the Gold Standard Assurance repositories:

- `back-end` - Spring Boot API
- `front-end` - Vite/React UI
- `data-migration` - Liquibase migrations used to create the local database
- `automation`, `images`, `infrastructure`, `load-test` - supporting exports

Use this README as the primary local setup walkthrough.

## Prerequisites

- Docker Desktop or OrbStack running
- Java 21
- Maven
- `nvm`
- Node.js `20.19.0`
- AWS CLI configured with SSO access to the account that can access the Development Cognito user pool

Check AWS access:

```bash
aws sts get-caller-identity
```

If your AWS CLI profile is not the default profile, export it before starting the backend:

```bash
export AWS_PROFILE=<aws-profile-name>
```

## Local Files

These local-only files are expected and ignored by git:

- `back-end/.env`
- `front-end/.env.local`

The backend env should point at the Development Cognito pool and local Postgres on port `15432`.

The frontend env should point Vite's `/api` proxy at the backend:

```bash
VITE_PROXY_TARGET=http://localhost:8080
```

## Database And Migrations

Start local Postgres and run Liquibase from the repo root:

```bash
cd /path/to/gs-assurance-platform
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
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml exec -T db \
  psql -U postgres -d assurance_db \
  -c 'select count(*) as changelog_rows from public.databasechangelog;'
```

## Seed Data

Liquibase creates the schema and reference/configuration data. It does not create the Proposal 2 demo users, accounts, or seeded projects by itself.

Apply the local Proposal 2 seed from the repo root:

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml exec -T db \
  psql -v ON_ERROR_STOP=1 -U postgres -d assurance_db \
  < back-end/local/seed-cross-linking-fixtures.sql
```

The seed is idempotent. It creates:

| User | Role/group | Local account |
| --- | --- | --- |
| `patslaurel` | `GS-Admin` | `null` |
| `pats-pd-alpha` | `Project-Developer` | `910001` - Cross Link Alpha PD |
| `pats-pd-beta` | `Project-Developer` | `910002` - Cross Link Beta PD |
| `pats-arm` | `ARM-Team` | `1` - GS ARM Team |
| `pats-vvb` | `VVB` | `910003` - Pats VVB |

It also creates five seeded Proposal 2 projects:

| GSID | Owner | Status |
| --- | --- | --- |
| `GS910101` | Cross Link Beta PD | New |
| `GS910102` | Cross Link Beta PD | Listed |
| `GS910103` | Cross Link Beta PD | Design Certified |
| `GS910104` | Cross Link Beta PD | Listed Real Case VPA |
| `GS910105` | Cross Link Beta PD | Design Certified Real Case VPA |

Verify the seed:

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml exec -T db \
  psql -U postgres -d assurance_db \
  -c 'select id, user_name, account_id, groups from gs."user" where id >= 910000 order by id;'

docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml exec -T db \
  psql -U postgres -d assurance_db \
  -c 'select id, gsid, name, project_status_id, account_id from gs.project where id between 910101 and 910105 order by id;'
```

## Reset Demo Data

To remove locally created demo requests/projects while keeping the seeded users/accounts and seeded Proposal 2 projects:

```bash
back-end/local/reset-cross-linking-demo-data.sh
```

Use this before a clean demo recording or walkthrough. It removes non-seeded local Proposal 2 request/project data, then reapplies `back-end/local/seed-cross-linking-fixtures.sql`.

For a full database reset, rerun all migrations and then apply the seed again:

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml down -v
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml up --build -d
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml exec -T db \
  psql -v ON_ERROR_STOP=1 -U postgres -d assurance_db \
  < back-end/local/seed-cross-linking-fixtures.sql
```

## Cognito Users

The app authenticates against the Development AWS Cognito pool:

```text
User pool: eu-central-1_XIzobZSx7
App client: 5vm30ddm7gk24bf4hpcin7p9cq
```

Local database users are not enough for browser login. Each login user must exist in Cognito and must have the matching local `gs."user"` row.

Create or confirm a Cognito user:

```bash
USER_POOL_ID=eu-central-1_XIzobZSx7
REGION=eu-central-1
USERNAME=<username>
EMAIL=<unique-email-address>
GROUP_NAME=<GS-Admin|Project-Developer|ARM-Team|VVB>

aws cognito-idp admin-get-user \
  --user-pool-id "$USER_POOL_ID" \
  --username "$USERNAME" \
  --region "$REGION"
```

If the user does not exist:

```bash
read -s TEMP_PASSWORD

aws cognito-idp admin-create-user \
  --user-pool-id "$USER_POOL_ID" \
  --username "$USERNAME" \
  --user-attributes \
    Name=email,Value="$EMAIL" \
    Name=email_verified,Value=true \
    Name=given_name,Value=Local \
    Name=family_name,Value=Demo \
  --temporary-password "$TEMP_PASSWORD" \
  --region "$REGION"

aws cognito-idp admin-add-user-to-group \
  --user-pool-id "$USER_POOL_ID" \
  --username "$USERNAME" \
  --group-name "$GROUP_NAME" \
  --region "$REGION"

aws cognito-idp admin-set-user-password \
  --user-pool-id "$USER_POOL_ID" \
  --username "$USERNAME" \
  --password "$TEMP_PASSWORD" \
  --permanent \
  --region "$REGION"
```

Use a unique email address per Cognito user. Plus-addressed emails are useful for local demo users.

Capture the Cognito `sub`:

```bash
aws cognito-idp admin-get-user \
  --user-pool-id "$USER_POOL_ID" \
  --username "$USERNAME" \
  --region "$REGION" \
  --query 'UserAttributes[?Name==`sub`].Value | [0]' \
  --output text
```

Then make sure the local database row has the same `user_name`, `idp_user_id`, `groups`, and `account_id`. The Proposal 2 seed already does this for the demo users listed above. If you create different users, add equivalent rows locally or update the seed for your walkthrough branch.

Local account rules:

| Role/group | Local DB rule |
| --- | --- |
| `GS-Admin` | `account_id` can be `null` |
| `Project-Developer` | `account_id` must be a Project Developer account |
| `ARM-Team` | `account_id` should be `1` for the seeded GS ARM Team account |
| `VVB` | `account_id` must be a VVB account |

If browser login succeeds but API calls fail with account or authorization errors, check the local row:

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml exec -T db \
  psql -U postgres -d assurance_db \
  -c 'select id, user_name, email_address, account_id, idp_user_id, groups, is_active from gs."user" order by id;'
```

If the backend was already running when you created a Cognito user, restart the backend so the startup user sync can run. For local demo work, the seed file remains the simplest source of truth for known demo users.

## Backend

Start the Spring Boot API in a new terminal:

```bash
cd /path/to/gs-assurance-platform/back-end
set -a
source .env
eval "$(aws configure export-credentials --format env)"
set +a
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

The AWS export command passes short-lived credentials from the active AWS CLI session into the Java process.

If startup fails with `The security token included in the request is expired`, refresh your AWS SSO session and restart the backend from a fresh terminal using the commands above. The backend syncs users from Cognito during startup, so stale AWS credentials will stop the app before login can work.

Health check:

```bash
curl http://localhost:8080/api/actuator/health
```

## Frontend

Start the Vite frontend in another terminal:

```bash
cd /path/to/gs-assurance-platform/front-end
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

## Walkthrough Checks

Use these checks before a team walkthrough:

```bash
curl http://localhost:8080/api/actuator/health

docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml ps

docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml exec -T db \
  psql -U postgres -d assurance_db \
  -c 'select count(*) as request_count from gs.request;'

docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml exec -T db \
  psql -U postgres -d assurance_db \
  -c 'select id, gsid, name from gs.project where id >= 910000 order by id;'
```

Expected clean demo state:

- Backend health is `UP`.
- Frontend opens at `http://localhost:3000`.
- `request_count` is `0`.
- Seeded projects are `GS910101` through `GS910105`.
- Demo users can log in through Cognito and match their local DB roles/accounts.

## Useful Commands

Stop local database containers:

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml down
```

Re-export AWS credentials after a fresh login:

```bash
eval "$(aws configure export-credentials --format env)"
```

Create a schema-only backup for handoff:

```bash
mkdir -p back-end/local/backups

docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml exec -T db \
  pg_dump -U postgres -d assurance_db --schema=gs --schema-only --no-owner --no-privileges \
  > back-end/local/backups/assurance_schema.sql
```

## Notes

- `back-end/local/compose.yml` mounts `../../data-migration/migrations`, which resolves to this repo's `data-migration/migrations` directory.
- `front-end/vite.config.ts` proxies `/api` to `VITE_PROXY_TARGET`, defaulting to `http://localhost:8080`.
- The app uses Development Cognito for local login, so users must exist in that Cognito pool.
- Do not commit passwords, AWS credentials, or local `.env` files.
