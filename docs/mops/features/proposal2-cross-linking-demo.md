---
name: proposal2-cross-linking-demo
description: Run a clean local Proposal 2 cross-linking demo and capture repeatable evidence.
---

# Proposal 2 Cross-Linking Demo

## Objective

Run the Proposal 2 cross-linking demo from a clean local database, verify the cross-link seed and smoke checks, capture dashboard/export/report evidence, and clean up local state.

## Prerequisites

- Use the repository root: `/Users/patslaurel/projects/gs-assurance-platform`.
- Keep Docker running.
- Use Java 21, Maven, Node.js 20.19.0, and npm.
- Keep `data-migration/migrations` present in this checkout. `back-end/local/compose.yml` mounts that path into Liquibase.
- Use `back-end/local/compose.local.yml` unless port `5432` is intentionally free. The override maps local Postgres to host port `15432`.
- For browser login with the React app, use Development Cognito credentials that can log in as Admin, ARM, and Project Developer users. The local SQL seed creates database users but does not create Cognito passwords.

## Stop Conditions

- Stop before any `down -v` command if the local Compose database contains data you need. Export that data first; this runbook intentionally destroys the local database volume.
- Stop if `docker compose ... logs liquibase` shows a failed migration.
- Stop if `curl http://localhost:8080/api/actuator/health` is not `UP`.
- Stop if `front-end/vite.config.ts` is not proxying `/api` to `http://localhost:8080` through `VITE_PROXY_TARGET`.
- Stop browser validation if the login user is not mapped to the intended local database account.
- Stop dashboard/export/report evidence capture if no cross-link child project has been created. The seed creates Beta parent data only; the demo must create Alpha-owned cross-link children through the app or API.

## Procedure

1. Reset and migrate the local database.

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml down -v
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml up --build -d
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml ps
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml logs liquibase
```

Success criteria: `db` is healthy and the Liquibase logs end without an error.

2. Apply the idempotent cross-link demo seed.

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml exec -T db \
  psql -U postgres -d assurance_db < back-end/local/seed-cross-linking-fixtures.sql
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml exec -T db \
  psql -U postgres -d assurance_db < back-end/local/seed-cross-linking-fixtures.sql
```

Success criteria: the second seed run completes without duplicate-key failures.

3. Verify migration and seed readiness.

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml exec -T db \
  psql -U postgres -d assurance_db -c "
    SELECT id, code, description, is_active
    FROM gs.project_classification
    WHERE id IN (10, 11)
    ORDER BY id;
  "

docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml exec -T db \
  psql -U postgres -d assurance_db -c "
    SELECT project_classification_id, request_type_id
    FROM gs.batch_mapping
    WHERE project_classification_id IN (10, 11)
    ORDER BY project_classification_id, request_type_id;
  "

docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml exec -T db \
  psql -U postgres -d assurance_db -c "
    SELECT id, gsid, name, project_classification_id, account_id, master_project_id, parent_project_id
    FROM gs.project
    WHERE id BETWEEN 910101 AND 910105
    ORDER BY id;
  "
```

Success criteria: classifications `10` and `11` exist, batch mappings exist for both IDs, and projects `910101` through `910105` exist under Cross Link Beta PD.

4. Start the backend for browser validation.

```bash
cd back-end
set -a
source .env
eval "$(aws configure export-credentials --format env)"
set +a
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

Success criteria:

```bash
curl http://localhost:8080/api/actuator/health
```

returns `UP`.

5. Start the frontend.

```bash
cd front-end
source ~/.nvm/nvm.sh
nvm install 20.19.0
nvm use 20.19.0
VITE_PROXY_TARGET=http://localhost:8080 npm start
```

Success criteria: `http://localhost:3000` opens and browser network calls go to `http://localhost:8080/api`.

6. Run the browser demo as Project Developer Alpha.

- Log in with a Project Developer Cognito user mapped to Cross Link Alpha PD.
- Open Create Project.
- Select `Real Case VPA (cross linkage)`.
- Select a Cross Link Beta PoA.
- Verify the Real Case selector is hidden.
- Finish the project and record the created project ID and GSID.
- Create another project with `Regular Case VPA (cross linkage)`.
- Select a Cross Link Beta PoA with no Real Cases and verify the empty state.
- Switch to a Cross Link Beta PoA with Real Cases.
- Select one Beta Real Case VPA and verify methodologies follow that Real Case.
- Finish the project and record the created project ID and GSID.

Success criteria: both created projects are Alpha-owned while their selected parents are Beta-owned.

7. Verify access boundaries and tree lineage.

- As Project Developer Alpha, open the project list and project tree. Verify the Alpha-owned cross-link children are visible.
- As Project Developer Beta, open the project list and direct URLs for the Alpha-created child IDs. Verify Beta cannot list or direct-GET those children.
- As Project Developer Alpha, open the cross-link child tree. Verify selected Beta ancestry is present and unrelated Beta siblings are absent.
- As Admin or ARM, open the created cross-link projects and verify the normal edit path is available.

Success criteria: parent PD ownership does not grant access to the Alpha-created children, and the tree shows only authorized lineage.

8. Prepare request rows for dashboard, export, and reporting evidence.

- For each created cross-link project, create or identify at least one request row through the normal UI path or the project request API.
- Record each request ID, request type, project ID, and project GSID in the evidence manifest.
- Use the query below to confirm the created projects have common-dashboard rows before capturing dashboard/export evidence.

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml exec -T db \
  psql -U postgres -d assurance_db -c "
    SELECT request_id, gsid, project_name, project_classification_id, account_name
    FROM gs.v_common_dashboard
    WHERE gsid IN ('<created-real-cross-link-gsid>', '<created-regular-cross-link-gsid>')
    ORDER BY request_id;
  "
```

Success criteria: both created cross-link project GSIDs have request rows in `gs.v_common_dashboard`, or the manifest records which project has no request row and why dashboard/export/report evidence is not yet complete.

9. Capture dashboard, export, and reporting evidence.

- As Admin or ARM, open the common dashboard.
- Filter or search for the created cross-link project GSIDs.
- Verify the project classification displays as `Real Case VPA` or `Regular Case VPA`, not the raw `(cross linkage)` label.
- Export the common dashboard report from the same filtered view.
- Open the generated spreadsheet and verify the classification column uses normal Real/Regular labels.
- Open Reporting, download the status report, and capture whether the created cross-link projects appear in the relevant status groupings.

Success criteria: dashboard DTOs and common-dashboard exports use normal labels for cross-link projects. Status report behavior is captured with the created project GSIDs or explicitly marked not applicable if no request/report row exists for the created projects.

10. Write the evidence manifest.

Create `docs/mops/evidence/proposal2-cross-linking-demo/<YYYY-MM-DD>/manifest.md` with:

- Demo date, operator, branch, and commit SHA.
- Login role and local account used for each browser pass.
- Created cross-link Real Case and Regular project IDs and GSIDs.
- Request IDs and request types used for dashboard, export, and reporting evidence.
- Screenshot, spreadsheet, and report file names.
- Commands or UI paths used for each artifact.
- Pass, fail, or not-applicable status for browser submit, access boundaries, tree lineage, dashboard, export, and reporting.

Success criteria: every manual evidence artifact can be traced to a role, account, created project, request row when applicable, timestamp, and command or UI path.

## Verification

Run the focused backend checks that cover Task 8 behavior:

```bash
mvn -f back-end/pom.xml test \
  -Dtest=ProjectApiIntegrationTest,ProjectClassificationSemanticsTest,CommonDashboardConverterTest,DashboardRequestSpecFactoryTest,CommonDashboardReportRowPopulatorTest,StatusReportApiIntegrationTest,ProjectAccessPolicyTest \
  -DfailIfNoTests=false
```

Run the frontend focused checks that cover create-flow and tree labels:

```bash
(cd front-end && $HOME/.nvm/versions/node/v20.19.0/bin/node ./node_modules/jest/bin/jest.js --runTestsByPath \
  src/components/Project/ProjectClassificationSemantics.test.ts \
  src/components/Project/create-project/steps/Step1.test.tsx \
  src/components/Project/create-project/steps/Step3.test.tsx \
  src/components/Project/create-project/createProject.test.tsx \
  src/components/Project/view-project/ViewProjectHierarchyTree.test.tsx \
  --coverage=false --maxWorkers=50%)
```

Success criteria: all focused checks pass, local seed can be run twice, and manual dashboard/export/report evidence is captured against created cross-link projects.

## Cleanup

Stop the foreground backend and frontend terminals, then stop local services and remove the local database volume:

```bash
# Press Ctrl-C in the backend and frontend terminal sessions first.
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml down -v
lsof -i :8080 -i :3000 || true
```

Success criteria: no Spring Boot or Vite terminal remains running, Compose services are stopped, and `lsof` shows no listener on ports `8080` or `3000`.

Remove temporary evidence files only after they have been copied into `docs/mops/evidence/proposal2-cross-linking-demo/<YYYY-MM-DD>/`.

## Troubleshooting

- If Postgres is unavailable on host port `5432`, use `back-end/local/compose.local.yml` and connect to host port `15432`.
- If backend startup fails with `The security token included in the request is expired`, refresh AWS credentials and restart the backend from a fresh terminal.
- If `nvm use 20.19.0` exits without activating Node, use the installed binary directly: `$HOME/.nvm/versions/node/v20.19.0/bin/node`.
- If browser login succeeds but API calls return authorization failures, confirm the Cognito subject maps to the intended `gs."user".idp_user_id` in the local database.
- If the dashboard or export has no rows for the created project, return to the request-row preparation step before treating dashboard/export/reporting as verified.

## References

- `docs/plan/2026-06-17_proposal2_cross_linking_todo.md`
- `docs/plan/2026-06-16_proposal2_cross_linking_final_plan.md`
- `docs/local-development-runbook.md`
- `back-end/local/compose.yml`
- `back-end/local/compose.local.yml`
- `back-end/local/seed-cross-linking-fixtures.sql`
