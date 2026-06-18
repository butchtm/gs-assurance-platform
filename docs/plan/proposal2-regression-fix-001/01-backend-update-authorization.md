# Task 01: Backend Update Authorization And Validation

Owner role: `developer`
Review role: `tech-lead`
Source of truth: `docs/plan/2026-06-18_proposal2_regression_fix_001.md`

## Goal

Restore main-branch update parity on the backend without weakening Proposal 2 cross-link validation.

Main parity to preserve:

| Role | Status | Backend result |
|---|---|---|
| Admin | Any status | Can update Project Developer Account, Project Classification, Project Title, and required parent hierarchy fields. |
| ARM | Any status | Preserve current backend behavior; current `ProjectAccessPolicy` allows ARM updates across statuses. |
| Owning PD | `New` | Can update only Project Title and Project Classification; cannot reassign Project Developer Account or mutate other project details through a tampered payload. |
| Owning PD | Listed or later | Cannot update. |
| Non-owning PD | Any status | Cannot update. |
| Linked parent PD | Any status | No access to the newly linked child project. |

## Current Problem

Proposal 2 added `ProjectService.validateUpdateKeepsAccountAndHierarchy(...)`, which rejects changes to:

- `accountId`
- `projectClassificationId`
- `masterProjectId`
- `parentProjectId`

That global rejection conflicts with main parity because Admin and likely ARM must still be able to correct these fields. It also risks blocking PD New-project classification edits.

## Files To Change

- `back-end/src/main/java/org/goldstandard/assurance/api/service/ProjectService.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/ProjectAccessPolicy.java`
- `back-end/src/test/java/org/goldstandard/assurance/api/service/ProjectAccessPolicyTest.java`
- `back-end/src/test/java/org/goldstandard/assurance/api/service/ProjectServiceTest.java`
- `back-end/src/test/java/org/goldstandard/assurance/api/ProjectApiIntegrationTest.java`

Only touch `PermissionsService.java` if existing role predicates are insufficient.

## Implementation Plan

1. Keep `ProjectAccessPolicy.validateCanUpdateProject(Project)` as the coarse authorization gate.
   - Admin returns true before any status check.
   - ARM returns true according to current behavior.
   - Project Developer returns true only when they own the project and the persisted project status is `New`.
   - Everyone else is denied.

2. Replace global immutability validation with role-aware validation.
   - Admin: allow account, classification, title, master project, and parent project changes in every status.
   - ARM: preserve current allowed behavior; same backend path as Admin unless main-browser parity proves otherwise.
   - Owning PD + `New`: allow title and classification changes only.
   - Owning PD + `New`: reject or deliberately preserve every other `ProjectUpdateDTO` field, including `accountId`, parent hierarchy, description, location, project type, dates, fee schedule, notes, crediting periods, transition details, SDGs, size, and assets.
   - Owning PD + non-New: denied by coarse gate before field validation.

3. Validate hierarchy only when needed.
   - If `projectClassificationId`, `accountId`, `masterProjectId`, or `parentProjectId` changes, compute the requested effective account and requested classification.
   - Use `CandidateParentEligibilityPolicy.validateParentEligibility(...)` with the requested hierarchy.
   - Cross-account parents remain valid only for cross-link classifications.
   - Normal Real Case / Regular VPA updates must still reject invalid cross-account parent combinations.

4. Preserve existing update side effects.
   - `ProjectUpdateApplier` still applies valid updates.
   - `ProjectCreditingPeriodFactory` still rebuilds crediting periods.
   - `ProjectTransactionService.performUpdate(...)` still persists and audits.
   - `externalUpdateExecutor.executeAsync(...)` still runs after valid updates.

## Acceptance Criteria

- Admin can update account/classification/title/hierarchy on New, Listed, and De-certified projects.
- ARM backend update behavior is unchanged and covered by tests.
- Owning PD can update title/classification only while the project is New.
- Owning PD cannot change `accountId` in any status.
- Owning PD cannot mutate non-title/classification fields through direct API payloads while the project is New.
- Owning PD cannot update once the project is Listed or later.
- Non-owning PD cannot update.
- Invalid hierarchy updates are rejected before persistence.
- Linked parent PD still cannot view or update the created cross-link child.

## Tests

Backend focused:

```bash
cd back-end
/opt/homebrew/bin/mvn -Dtest=ProjectAccessPolicyTest,ProjectServiceTest,ProjectApiIntegrationTest test
```

Add or update:

- `ProjectAccessPolicyTest`
  - Admin can update every status.
  - ARM can update every status unless browser parity later proves a stricter rule.
  - Project Developer owner can update only New.
  - Project Developer non-owner cannot update.

- `ProjectServiceTest`
  - Admin account/classification/hierarchy change reaches `ProjectUpdateApplier`.
  - PD New classification/title update is allowed.
  - PD New account change is rejected.
  - PD New tampered payload cannot mutate description, location, project type, dates, fee schedule, notes, crediting periods, transition details, parent hierarchy, SDGs, size, assets, or other non-title/classification fields.
  - Invalid parent hierarchy is rejected before transaction/update applier.

- `ProjectApiIntegrationTest`
  - Admin updates New, Listed, and De-certified projects.
  - PD updates own New project title/classification.
  - PD New direct API payload attempting to change account and non-title/classification details is rejected or leaves persisted values unchanged.
  - PD Listed update is rejected.
  - Linked parent PD direct update/access remains rejected.

## Do Not Do

- Do not add new roles.
- Do not add new dependencies.
- Do not make Project Developer account reassignment possible.
- Do not remove Proposal 2 cross-link hierarchy validation.
- Do not make status updates happen through the general project update endpoint.
