# Task 03: ARM Local User And Parity Validation

Owner role: `developer`
Review role: `tech-lead`
Source of truth: `docs/plan/2026-06-18_proposal2_regression_fix_001.md`

## Goal

Create or enable a local ARM login path so ARM update behavior can be validated before the demo.

The regression fix must preserve ARM behavior. We cannot honestly claim that without a real ARM browser pass.

This task is a prerequisite before finalizing any ARM-specific frontend or backend behavior. If `origin/main` browser parity differs from current backend assumptions, update Tasks 01, 02, and 05 before implementation.

## Current Problem

Local browser login uses Development Cognito. Database seed rows alone are not enough.

`AuthService.validatePlatformAccount(...)` rejects non-admin/non-support users without a platform account. An ARM Cognito user must sync into local `gs."user"` and be mapped to the seeded GS ARM Team account.

## Files To Reference

- `README.md`
- `docs/local-development-runbook.md`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/auth/AuthService.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/user/UserSyncOnStartUpListener.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/user/UserSyncService.java`
- `front-end/src/PermissionGroup.ts`
- `data-migration/migrations/000190-create-account-types.sql`
- `data-migration/migrations/000205-create-gold-standard-accounts.sql`

## Implementation Plan

1. Create a Development Cognito ARM user.
   - Suggested username: `pats-arm`
   - Group: `ARM-Team`
   - Email: user-owned email address
   - Do not commit passwords.
   - Do not write temp credentials into docs.
   - Use the active AWS profile/region for Development Cognito and record only the profile/region name, not credentials.
   - Run an idempotency preflight with `admin-get-user` before creating the user.
   - Prefer `admin-set-user-password --permanent` with a local, non-committed password for demo use, or complete the Cognito new-password challenge in the browser if a temporary password is used.

2. Sync the user into local DB.
   - Restart backend or run the existing sync path.
   - Confirm the user exists in `gs."user"`.
   - Set `account_id = 1` if Cognito sync does not attach the seeded GS ARM Team account.

3. Validate login.
   - Log in at `http://localhost:3000`.
   - Confirm authenticated user has `ARM-Team` through `/api/public/verify-token`.
   - Confirm the app does not show "account not assigned" behavior.

4. Use the ARM user for parity testing.
   - Open an existing New project.
   - Open an existing Listed or later project.
   - Record which fields are editable on current `origin/main`, not a stale local `main`.
   - Verify the feature branch preserves the same behavior.
   - Capture browser evidence for New and non-New projects: account, classification, title, linked PoA/Real Case fields, later wizard steps, and submit behavior.

5. Clean up ownership after demo if the user was created only for local validation.
   - Disable the temporary Cognito user or document who owns it.
   - Keep local DB cleanup/reset instructions out of committed secrets.

## Suggested Commands

Use the existing Development Cognito path. Do not commit the values from your shell history if they include secrets.

```bash
USER_POOL_ID=eu-central-1_XIzobZSx7
USERNAME=pats-arm
EMAIL=patslaurel@thinkingmachin.es
aws cognito-idp admin-get-user \
  --user-pool-id "$USER_POOL_ID" \
  --username "$USERNAME" \
  --region eu-central-1

read -s TEMP_PASSWORD

aws cognito-idp admin-create-user \
  --user-pool-id "$USER_POOL_ID" \
  --username "$USERNAME" \
  --user-attributes \
    Name=email,Value="$EMAIL" \
    Name=email_verified,Value=true \
    Name=given_name,Value=Pats \
    Name=family_name,Value=ARM \
  --temporary-password "$TEMP_PASSWORD" \
  --region eu-central-1

aws cognito-idp admin-add-user-to-group \
  --user-pool-id "$USER_POOL_ID" \
  --username "$USERNAME" \
  --group-name ARM-Team \
  --region eu-central-1

aws cognito-idp admin-set-user-password \
  --user-pool-id "$USER_POOL_ID" \
  --username "$USERNAME" \
  --password "$TEMP_PASSWORD" \
  --permanent \
  --region eu-central-1
```

Verify local DB mapping after backend sync:

```bash
docker exec local-db-1 psql -U postgres -d assurance_db \
  -c "select id, user_name, groups, account_id, is_active from gs.\"user\" where user_name = 'pats-arm';"

docker exec local-db-1 psql -U postgres -d assurance_db \
  -c "update gs.\"user\" set account_id = 1, is_active = true where user_name = 'pats-arm' and account_id is null;"
```

## Acceptance Criteria

- ARM Cognito user exists in group `ARM-Team`.
- Local backend sync imports the ARM user.
- Local `gs."user"` row is active and mapped to account `1`.
- ARM login works in browser.
- `/api/public/verify-token` confirms `ARM-Team`.
- ARM update behavior is documented from current `origin/main`.
- Feature branch matches the main ARM behavior.

## 2026-06-18 Local Implementation Evidence

Status: local ARM authentication setup is ready for browser parity testing, but full ARM parity is not complete yet.

Completed:

- Development Cognito user `pats-arm` exists in user pool `eu-central-1_XIzobZSx7`.
- `pats-arm` is enabled, confirmed, and assigned to Cognito group `ARM-Team`.
- Local `gs."user"` has `pats-arm` active with `groups = {ARM-Team}` and `account_id = 1` (`GS ARM Team`).
- `POST http://localhost:3000/api/public/login` succeeds for `pats-arm`.
- `GET http://localhost:3000/api/public/verify-token` returns `authenticated = true`, `groups = ["ARM-Team"]`, and account `GS ARM Team`.

Local-only exception:

- Admin-triggered `POST http://localhost:3000/api/user-synchronizations` returned HTTP 500 during this setup pass.
- To unblock local validation, the local DB row was repaired manually to mirror the confirmed Cognito identity and map it to `GS ARM Team`.
- Do not treat that manual repair as proof that the supported user-sync path is healthy; capture the backend 500 root cause before marking this task fully done.

Remaining:

- Run ARM browser parity on current `origin/main` for one `New` project and one `Listed` or later project.
- Repeat the same ARM browser parity pass on `feat/proposal-2-cross-linking`.
- Record editability for Project Developer Account, Project Classification, Project Title, linked PoA/Real Case fields, later wizard steps, and submit behavior.

## Do Not Do

- Do not commit passwords.
- Do not commit AWS credentials.
- Do not invent a DB-only bypass login path.
- Do not change production Cognito configuration.
- Do not widen ARM permissions beyond observed main/backend parity.
