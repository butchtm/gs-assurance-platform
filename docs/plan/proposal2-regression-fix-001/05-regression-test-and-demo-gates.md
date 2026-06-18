# Task 05: Regression Test And Demo Gates

Owner role: `test-engineer`
Review role: `tech-lead`
Source of truth: `docs/plan/2026-06-18_proposal2_regression_fix_001.md`

## Goal

Prove the regression fix does not break Proposal 2 and does not regress main parity.

This task is the gate before recording another demo.

## Required Test Matrix

| Role | Scenario | Expected result |
|---|---|---|
| Admin | Edit New project account/classification/title/parents | Persists. |
| Admin | Edit Listed or later project account/classification/title/parents | Persists if hierarchy is valid. |
| ARM | Edit New project | Matches main/backend parity. |
| ARM | Edit Listed or later project | Matches main/backend parity. |
| Owning PD | Edit New project title/classification | Persists. |
| Owning PD | Edit New project account | Rejected or persisted account remains unchanged. |
| Owning PD | Tamper New project non-title/classification fields | Rejected or persisted protected values remain unchanged. |
| Owning PD | Edit Listed or later project | Blocked or update unavailable. |
| Non-owning PD | Any update | Denied. |
| Linked parent PD | List/tree/direct-open/update/action child cross-link project | Denied or pruned from results. |
| Alpha PD | Create cross-link Real Case VPA under Beta PoA | Persists under Alpha ownership. |
| Alpha PD | Create cross-link Regular Case VPA under Beta PoA/Real Case | Persists under Alpha ownership. |
| Alpha PD | Candidate Beta PoAs across statuses | New, Listed, and Design Certified eligible PoAs are available. |
| Alpha PD | Regular cross-link Real Case selector | Real Case options are filtered under the selected linked PoA. |
| Alpha PD | Normal Real/Regular VPA create | Same-account rules remain unchanged; cross-account parents are rejected. |
| Admin | Search/list cross-link projects | Rows visible. |
| Dashboard/report/export | Cross-link rows and labels | Rows are included where expected and labels remain normal Real/Regular unless product changes decision. |

## Backend Test Commands

```bash
cd back-end
/opt/homebrew/bin/mvn -Dtest=ProjectAccessPolicyTest,ProjectServiceTest,ProjectApiIntegrationTest test

/opt/homebrew/bin/mvn -Dtest=CommonDashboardConverterTest,DashboardRequestSpecFactoryTest,CommonDashboardReportRowPopulatorTest,StatusReportApiIntegrationTest test
```

Expected backend coverage:

- Role/status update matrix.
- Field-level update validation.
- Candidate parent eligibility after account/classification/parent edits.
- Linked parent PD access denial for direct GET, update/action attempts, and list/tree/dashboard/report surfaces where applicable.
- Cross-link create behavior.
- Normal Real/Regular VPA behavior remains same-account unless cross-link classification is used.
- Dashboard/report/export behavior includes cross-link rows with accepted normal Real/Regular labels.

## Frontend Test Commands

```bash
cd front-end
$HOME/.nvm/versions/node/v20.19.0/bin/node ./node_modules/jest/bin/jest.js --runTestsByPath \
  src/components/Project/update-project/UpdateProject.test.tsx \
  src/components/Project/create-project/steps/Step1.test.tsx \
  src/components/Project/create-project/steps/ProjectTitle.test.tsx \
  --coverage=false \
  --maxWorkers=50%
```

Add `ProjectList.test.tsx` to the command if Task 04 changes list columns. Add `ProjectTreeList.test.tsx` only if Task 04 changes `ProjectTreeList.tsx` or project-developer tree behavior.

Expected frontend coverage:

- Admin all-status edit fields enabled.
- PD New-only classification/title edit.
- PD account field protected.
- PD New protected Step2-Step5 fields cannot be changed through the update UI.
- Payload includes changed authorized fields.
- Payload preserves protected fields.
- Create wizard cross-link options still visible.

## Browser Demo Gates

Run on `http://localhost:3000`.

1. Reset local data to seeded state.
2. Log in as Admin.
   - Edit a New project.
   - Edit a Listed or later project.
   - Confirm account/classification/title changes persist.
3. Log in as ARM.
   - Confirm ARM login works.
   - Confirm ARM edit behavior matches main parity.
4. Log in as Alpha PD.
   - Create cross-link Real Case VPA.
   - Create cross-link Regular Case VPA.
   - Confirm Alpha owns both.
   - Confirm Beta PoA candidates include expected New, Listed, and Design Certified examples.
   - Confirm the Regular cross-link Real Case selector only shows Real Cases under the selected linked PoA.
   - Confirm normal Real Case VPA and normal Regular Case VPA still enforce same-account parent selection.
5. Log in as Beta PD.
   - Confirm Beta cannot list, tree-view, direct-open, update, or action Alpha child projects.
6. Verify project list/dashboard/report behavior.
   - Admin can find created projects.
   - Dashboard table includes expected rows and PD/account dimension.
   - Export artifact includes expected rows with normal Real/Regular labels.
   - Status-report output includes expected rows, or the test notes why the created local project is not reportable.
   - Dashboard/report labels remain normal Real/Regular unless product asks otherwise.

## Acceptance Criteria

- All focused backend tests pass.
- All focused frontend tests pass.
- Browser gates pass for Admin, ARM, Alpha PD, and Beta PD.
- No passwords or external credentials are committed.
- Demo notes clearly separate normal Regular VPA from `Regular Case VPA (cross linkage)`.

## Do Not Do

- Do not record a demo until ARM login has been verified.
- Do not call a candidate-parent details exposure issue resolved unless product accepts it.
- Do not use stale local `main` as parity evidence; use current `origin/main`.
