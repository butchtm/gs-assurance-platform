# Task 04: Projects Classification Visibility

Owner role: `developer`
Review role: `tech-lead`
Source of truth: `docs/plan/2026-06-18_proposal2_regression_fix_001.md`

## Goal

Resolve the admin feedback that the new classifications are not visible in Projects without breaking the email-confirmed analytics/reporting behavior.

## Current Understanding

This is not proven as a missing-row API bug.

Known behavior:

- `/projects` can return rows with raw cross-link classification IDs `10` and `11`.
- The Projects table has no Project Classification column.
- Detail/dashboard/reporting surfaces intentionally normalize labels to `Real Case VPA` and `Regular VPA`.
- The email says analytics/reporting does not need separate cross-link labels as long as PD/account dimension is clear.

## Files To Inspect Or Change

- `front-end/src/components/Project/project-list/ProjectList.tsx`
- `front-end/src/components/Project/project-list/ProjectTreeList.tsx`
- `front-end/src/components/Project/Project.tsx`
- `front-end/src/common/data-types/DataReferenceTypes.tsx`
- `back-end/src/main/java/org/goldstandard/assurance/api/controller/ProjectController.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/converter/ProjectConverter.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/ProjectFinder.java`
- `data-migration/migrations/002190-add-searchable-projects-view.sql`
- `data-migration/migrations/002260-add-project-tree-node-view.sql`
- `data-migration/migrations/003680-add-cross-linking-project-classifications.sql`

## Implementation Plan

1. First prove the failure mode.
   - As Admin, call `/api/projects?search=<cross-link GSID>`.
   - Confirm whether the row exists.
   - Confirm whether `projectClassificationId` is present.
   - Check browser Projects list for row visibility.

2. If rows are missing, fix backend search only.
   - Check `ProjectFinder` and search view behavior.
   - Do not make inactive cross-link classifications active just to make rows appear.
   - Add integration coverage for IDs `10` and `11`.

3. If rows exist but classification labels are missing, use the smallest UI fix.
   - Add a Project Classification column to admin/ARM operational project list only if product wants this distinction there.
   - Resolve labels from raw `projectClassificationId` and reference data.
   - Keep detail/dashboard/report normalization unchanged unless product reverses the email decision.

4. Keep cross-link reference scoping tight.
   - `getCustomReferenceOptions("ProjectClassification")` currently allowlists inactive cross-link classifications.
   - Audit callers before widening it further.
   - Prefer a local selector option helper over globally active reference rows.

## Acceptance Criteria

- Admin can find cross-link projects in `/projects`.
- If product wants list-level classification visibility, Admin/ARM sees a Project Classification column.
- Dashboard/report/export labels remain normal `Real Case VPA` / `Regular VPA`.
- PD visibility rules do not change.
- Linked parent PD still cannot list or open the created child.

## Tests

Backend, if search/API changes:

```bash
cd back-end
/opt/homebrew/bin/mvn -Dtest=ProjectApiIntegrationTest test
```

Frontend, if list UI changes:

```bash
cd front-end
$HOME/.nvm/versions/node/v20.19.0/bin/node ./node_modules/jest/bin/jest.js --runTestsByPath \
  src/components/Project/project-list/ProjectList.test.tsx \
  --coverage=false \
  --maxWorkers=50%
```

Add or update:

- Admin `/projects` returns cross-link rows.
- Projects list renders cross-link rows.
- Create `ProjectList.test.tsx` if this task changes the project-list UI and no focused test exists yet.
- Add `ProjectTreeList.test.tsx` to the command only if this task changes `ProjectTreeList.tsx` or project-developer tree behavior.
- Classification column renders raw cross-link labels only where product wants it.
- Dashboard/export/report tests continue normalized labels.

## Do Not Do

- Do not undo dashboard/report label normalization.
- Do not globally activate cross-link classifications without product approval.
- Do not add a classification filter unless product asks for it.
- Do not change PD access rules while fixing Admin list visibility.
