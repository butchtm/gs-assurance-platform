# Proposal 2 Regression Fix Plan

Date: 2026-06-18
Branch: `feat/proposal-2-cross-linking`
Scope: Planning only. No implementation in this pass.

## Task Files

- `docs/plan/proposal2-regression-fix-001/01-backend-update-authorization.md`
- `docs/plan/proposal2-regression-fix-001/02-frontend-update-editability.md`
- `docs/plan/proposal2-regression-fix-001/03-arm-local-user-and-parity.md`
- `docs/plan/proposal2-regression-fix-001/04-projects-classification-visibility.md`
- `docs/plan/proposal2-regression-fix-001/05-regression-test-and-demo-gates.md`

## Main-Parity Implementation Plan

This section supersedes the earlier New-only admin assumption. It is based on local `main` testing on 2026-06-18 for Admin/PD behavior, with ARM behavior still requiring current `origin/main` browser parity evidence:

- Admin can edit Project Developer Account, Project Classification, and Project Title in every project status from `New` through `Listed` to `De-certified`.
- Project Developer users can edit their own Project Classification and Project Title only while the project status is `New`.
- Project Developer users must not be able to reassign the Project Developer Account.
- ARM behavior must be preserved, not guessed. Current backend policy allows ARM updates across statuses; browser parity needs a real local ARM user.

### Role And Field Matrix

| Role | Project Status | Project Developer Account | Project Classification | Project Title | Linked PoA / Real Case Parent Fields | Expected Access |
|---|---|---|---|---|---|---|
| Admin | Any status | Editable | Editable | Editable | Editable when required by classification; backend validates hierarchy | Can update |
| ARM | Any status | Preserve current main behavior after Task 03 verifies it | Preserve current main behavior after Task 03 verifies it | Preserve current main behavior after Task 03 verifies it | Preserve current main behavior after Task 03 verifies it | Can update if main parity confirms |
| Owning PD | `New` | Read-only / preserved | Editable | Editable | Not editable unless client later expands PD scope | Can update only title/classification |
| Owning PD | Listed or later | Not editable | Not editable | Not editable | Not editable | Update unavailable or backend rejected |
| Non-owning PD | Any status | Not editable | Not editable | Not editable | Not editable | No access |
| Linked parent PD | Any status | Not editable | Not editable | Not editable | Not editable | No visibility/action on child |

### Plan To Implement

1. Backend update authorization and validation.
   - Keep `ProjectAccessPolicy.validateCanUpdateProject(...)` as the coarse access gate.
   - Make the role/status branches explicit:
     - Admin: allowed for every status.
     - ARM: preserve current allowed behavior after Task 03 records main-browser parity, then lock it with tests.
     - Project Developer: allowed only for own project while current persisted status is `New`.
   - Replace the Proposal 2 global account/classification/master/parent immutability rule with role-aware, field-aware validation.
   - Admin and ARM changes to account/classification/hierarchy must reach `ProjectUpdateApplier`.
   - Project Developer changes must preserve account and every field other than title/classification, reject non-owning updates, and reject any update when persisted status is not `New`.
   - When classification or parent fields change, validate with `CandidateParentEligibilityPolicy`; cross-account parents remain allowed only for cross-link classifications.

2. Frontend update form capabilities.
   - Replace `isUpdate` as the blanket disabled-state driver with explicit field capabilities:
     - `canEditDeveloperAccount`
     - `canEditProjectClassification`
     - `canEditProjectTitle`
     - `canEditParentHierarchy`
   - Admin: all true in every status.
   - ARM: preserve parity once tested with the local ARM user.
   - Owning PD + `New`: classification/title true; developer account false; parent hierarchy false unless client later expands PD scope.
   - Owning PD + non-New and non-owning PD: update unavailable or all protected.
   - `UpdateProject.tsx` must stop overwriting changed account/classification/master/parent fields for roles that are allowed to edit them.
   - Unauthorized roles should deliberately preserve original protected values or be blocked before submit.

3. ARM local test user and parity evidence.
   - Create a Development Cognito user such as `pats-arm` in group `ARM-Team`.
   - Restart/sync backend so the user appears in `gs."user"`.
   - Ensure local `gs."user".account_id = 1`, the seeded GS ARM Team account.
   - Do not commit passwords or temp credentials.
   - Record `origin/main` ARM browser evidence before finalizing ARM-specific code paths on the feature branch.
   - Add this as a hard pre-demo prerequisite before ARM browser validation.

4. Projects classification visibility.
   - Keep analytics/report/detail normalization to normal `Real Case VPA` / `Regular VPA`, because the email says analytics does not need separate cross-link labels.
   - For admin/ARM operational project lists, add or test a visible Project Classification column only if the product wants operators to distinguish cross-link rows there.
   - Do not make inactive cross-link classifications globally active as a shortcut.

### Tests To Add Or Update

Backend:
- `ProjectAccessPolicyTest`: explicit role/status matrix for Admin, ARM, owning PD New, owning PD non-New, non-owning PD.
- `ProjectServiceTest`: remove tests that globally reject account/hierarchy changes; replace with role-aware success and rejection cases.
- `ProjectApiIntegrationTest`: Admin updates account/classification/title on New, Listed, and De-certified statuses.
- `ProjectApiIntegrationTest`: ARM parity cases after local/browser behavior is confirmed.
- `ProjectApiIntegrationTest`: owning PD updates title/classification on New, cannot update account or other project details, and cannot update any protected fields once Listed.
- `ProjectApiIntegrationTest`: invalid hierarchy still rejected when account/classification/parent changes produce a non-cross-link cross-account parent.

Frontend:
- `UpdateProject.test.tsx`: Admin update form is available and fields are enabled for New, Listed, and De-certified projects.
- `UpdateProject.test.tsx`: owning PD New can edit title/classification but cannot edit developer account.
- `UpdateProject.test.tsx`: owning PD Listed sees update unavailable or disabled/protected fields, matching main parity.
- `Step1.test.tsx`: field capability props control account/classification/title/parent disabled states.
- `ProjectTitle.test.tsx` or existing Step1 coverage: disabled title field stays disabled without breaking PII warnings/character count.
- Project list tests if a classification column is added.

Browser gates before demo:
- Admin edits Project Developer Account, Project Classification, and Project Title on at least one New and one non-New project.
- PD edits Project Classification and Project Title on own New project.
- PD cannot edit once the project is Listed.
- ARM login works and ARM behavior matches main parity.
- Alpha PD can create cross-link Real Case VPA and cross-link Regular Case VPA using eligible Beta parents across New, Listed, and Design Certified parent statuses.
- Regular cross-link Real Case choices are filtered under the selected linked PoA.
- Normal Real Case VPA and normal Regular Case VPA creation remain same-account and reject cross-account parents.
- Linked parent PD still cannot list, tree-view, direct-open, update, action, or otherwise gain dashboard/report visibility into the newly created cross-link child.
- Dashboard/report/export evidence includes cross-link rows where expected while preserving accepted normal Real/Regular labels.

## Parity And Regression Findings

Sources reviewed:
- Proposal 2 PDF: `/Users/patslaurel/Downloads/AM-Cross linking regular case VPAs with different PDs-090626-025419.pdf`
- Gmail thread: `Thinking Machines Test Feature: Cross-Linking VPAs for Assurance`
- Local demo summary from 2026-06-18
- Six-agent paired code review across admin editability, project visibility, and broader Proposal 2 regression surface
- Branch comparison: `d37fc36..HEAD` and `origin/main..HEAD` both show the same focused Proposal 2 delta; local `main` is stale at `7a17219` and includes unrelated extraction/local-dev noise.

Bottom line:
- Proposal 2 create behavior is mostly aligned with the email/PDF: new cross-link labels exist, PD users can create cross-link VPAs, Regular Case cross-link requires both PoA and Real Case VPA selection, all PoAs/Real Case VPAs under the chosen parent are intended to be available, and linked parent PDs must not gain child visibility.
- We introduced at least one release-blocking parity regression: admin correction of account/classification/title/hierarchy fields is blocked or stripped by Proposal 2 changes even though main allows Admin edits across statuses. ARM needs a real local user to verify parity, and backend behavior currently says ARM should remain allowed across statuses.
- The reported "admin cannot see the new project classifications in Projects" is not currently proven as a missing-row API bug. It is a visibility/label gap: `/projects` can return rows with raw classification IDs `10/11`, but the Projects table has no classification column and other surfaces intentionally normalize cross-link labels to `Real Case VPA` / `Regular VPA`.

| Area | Source Requirement Or Baseline | Current Branch Behavior | Finding | Severity | Required Action |
|---|---|---|---|---|---|
| Admin all-status edit parity | Main testing confirms admin can edit Project Developer Account, Project Classification, and Project Title in every status from `New` through `De-certified`. Email from Annyta says to enable edit access for ARM accounts. PDF says Update Project should not need extra changes because it reuses wizard steps. | `Step1` disables Project Developer Account, Project Classification, Programme of Activities, and Real Case VPA whenever `isUpdate` is true. `UpdateProject` overwrites submitted account/classification/master/parent values with originals. `ProjectService` rejects those changes for every role/status. | Confirmed regression. | P1 | Admin must be able to edit account/classification/title/hierarchy across statuses. Backend validates invalid hierarchy but must not globally strip or reject allowed fields. |
| ARM parity | Existing backend policy allows ARM update access across statuses, and email asks to enable ARM edit access. Browser parity still needs a real ARM user. | Current Proposal 2 branch blocks shared update fields through the same UI/backend path that blocks admin. | Confirmed regression risk until ARM browser parity is verified. | P1 | Create/sync local ARM user and preserve main/backend ARM edit behavior. Add ARM browser and integration checks. |
| Project Developer New-only title/classification edit | Main testing confirms PD can edit own Project Classification and Project Title while status is `New`, but cannot once Listed. | PD update flow is locked for these fields in Proposal 2 branch if blanket `isUpdate` disabling is used. | Confirmed parity requirement. | P1 | Owning PD can update title/classification only while persisted status is `New`; non-New PD update remains blocked. |
| Project Developer account edit by PD | Meerim explicitly asks whether PD can edit Project Developer Account for a newly created VPA. | Current backend/frontend prevent PD account movement. | Likely correct until confirmed. | P2 | Do not allow PD account reassignment by default. Ask client to confirm. |
| Cross-link labels in Create Project | Email confirms `Real Case VPA (cross linkage)` and `Regular Case VPA (cross linkage)` should be selectable for PD users. | Create flow exposes these labels through inactive-but-allowlisted classifications. | Aligned. | None | Keep. Regression test the selector remains visible only where intended. |
| Cross-link labels in dashboards/reports/tree | Email says no need to separate standard and cross-linked Real/Regular for analytics; label as normal Real/Regular is acceptable as long as PD name is clearly marked. | Detail, tree, dashboard/export/report paths normalize cross-link labels to canonical Real/Regular labels. | Aligned for analytics/reporting. | None | Keep normalization on analytics/report surfaces. Verify PD name is visible where reports need that distinction. |
| Cross-link labels in admin Projects list | User feedback says new classifications are not seen in Projects when signed in as admin. | Admin list shows no classification column. Backend overview DTO preserves only raw `projectClassificationId`, not a visible label. Detail view normalizes the displayed classification. | Product-facing visibility gap. Not proven as missing project rows. | P2 | Add a focused admin Projects-list check. If product wants raw labels there, add an admin/ARM-only Classification column using raw `projectClassificationId`; do not undo global normalization. |
| Cross-link rows in `/projects` API | Admin should see projects they are allowed to administer. | Local API evidence and code review indicate `/projects` can return rows with raw IDs `10/11`; search views do not filter out inactive classifications. | Likely OK, but needs locked regression coverage. | P3 | Add integration/browser check proving admin `/projects` returns and renders cross-link GSIDs. |
| Regular Case VPA cross-link flow | PDF Proposal 2 and email say Regular Case cross-link requires selecting both linked PoA and corresponding Real Case VPA. | Implemented. Demo-summary wording said "Regular case VPA: no cross-linking, POA selector empty," which conflicts with Proposal 2/email and likely describes the normal Regular VPA option, not the new cross-link option. | Clarification needed in demo script, not code regression. | P2 | Demo both: normal Regular VPA remains same-account only; `Regular Case VPA (cross linkage)` shows cross-account PoA plus Real Case VPA. |
| All PoA statuses available for cross-link | Email says all PoAs across all statuses, including New, Listed, and Design Certified, should be included. Follow-up says New PoAs/VPAs may run in parallel. | Candidate APIs include cross-account parents broadly, including New. Candidate-parent details returns rich detail for selected parent. | Aligned on availability, but detail exposure needs explicit product/security acceptance. | P2 | Keep all-status candidate availability if product confirms. Consider minimal/redacted candidate detail for non-public cross-account parents if exposure is a concern. |
| Linked parent PD visibility | Email says only the target VPA owner can view; PD accounts associated with linked PoA or linked VPA shall not have visibility or action allowed. Demo confirms Beta cannot see Alpha child. | Direct `GET /projects/{id}` remains denied for linked parent PD; tree/list access is pruned. | Aligned. | None | Keep. Preserve existing denial tests. |
| Candidate-parent detail vs direct URL denial | Direct URL denial is used as proof linked parent PD has no access. | The candidate-parent-details endpoint can still hydrate cross-account New parent details for selection while direct project access is denied. | Product/security decision needed. | P2 | Decide whether candidate detail is allowed as registry/selection data. If allowed, document it. If not, return a minimal projection or block non-public parents. |
| Normal existing VPA behavior | Email says existing Regular/Real VPA behavior must remain unchanged. | Code broadens Real/Regular helper semantics to include cross-link IDs, which is intended for shared behavior. Need explicit parity tests for normal native Real/Regular creation and update paths. | Regression risk, not confirmed failure. | P2 | Add normal-flow regression tests beside cross-link tests: same-account parent enforcement, dashboard filters, request/batch behavior, and update of non-linkage fields. |
| Inactive cross-link classifications in reference helper | Cross-link classes should behave as special create options, not ordinary active categories everywhere. | Frontend `getCustomReferenceOptions("ProjectClassification")` globally allowlists inactive cross-link classifications. | Scope leak risk. | P3 | Scope the allowlist to create/update classification selectors or audit all callers and document intentional usage. |

## Regression Table To Use Before Demo

| Demo/Regression Gate | Expected Result | Current Status | Severity If Failing |
|---|---|---|---|
| Normal Real Case VPA create as PD | Same-account PoA only; cross-account PoA rejected. | Needs rerun after fixes. | P1 |
| Normal Regular Case VPA create as PD | Same-account PoA and Real Case VPA only; cross-account hierarchy rejected. | Needs rerun after fixes. | P1 |
| Cross-link Real Case VPA create as PD Alpha | Alpha can select Beta PoA across statuses and project remains owned by Alpha. | Previously demoed. Needs rerun after reset/fixes. | P1 |
| Cross-link Regular Case VPA create as PD Alpha | Alpha selects Beta PoA, then Beta Real Case VPA under that PoA; project remains owned by Alpha. | Previously demoed. Needs rerun after reset/fixes. | P1 |
| Linked parent PD Beta direct access to Alpha child | Beta cannot list or direct-open Alpha-owned cross-link child. | Previously demoed. Must preserve. | P1 |
| Admin Projects page | Admin can find cross-link GSIDs; classification visibility decision is explicit. | Row visibility likely OK; label visibility gap remains. | P2 |
| Admin update any-status project | Admin can edit Project Developer Account, Project Classification, Project Title, and required parent fields across statuses; invalid hierarchy is rejected. | Failing now. | P1 |
| ARM update parity | ARM local user can log in and edit according to main/backend parity. | Needs Cognito user and browser validation. | P1 |
| PD update New project | Owning PD can edit Project Classification and Project Title while status is `New`; account remains protected. | Failing or under-covered now. | P1 |
| PD update non-New project | Owning PD cannot edit Project Classification or Project Title once status is Listed or later. | Must preserve. | P1 |
| Dashboard/report/export labels | Cross-link rows roll up as normal Real/Regular labels; PD/account dimension remains clear. | Backend tests cover parts; browser/export evidence still incomplete. | P2 |
| Candidate-parent details | Cross-account parent details are either accepted as selection/registry data or intentionally minimized. | Open decision. | P2 |

## Assumptions

- Admin users should be able to edit Project Developer Account, Project Classification, Project Title, and required parent hierarchy fields in every status, matching observed main behavior.
- ARM users should keep current behavior. Backend currently permits ARM updates across statuses, but browser parity requires a local ARM Cognito user.
- Project Developer users should be able to edit their own Project Classification and Project Title only while the project status is `NEW`.
- Non-admin project-developer users should not gain broader cross-account edit capability from this fix.
- Cross-link classifications remain inactive reference data to avoid making them ordinary active classifications everywhere.
- Cross-link create options should stay available where Proposal 2 intended them, but display/detail surfaces may continue normalizing them to Real Case VPA or Regular VPA.
- The reported "not visible in Projects" issue could mean either cross-link project rows are absent from `/projects`, or the cross-link classification labels are normalized and therefore not visibly distinct. The implementation must verify which symptom is real before changing data semantics.

## Open Questions

- Does ARM browser behavior on main exactly match backend policy, meaning all-status update access, or are there frontend restrictions we need to preserve?
- If an admin changes a cross-link project classification to a non-cross-link classification, should the previous cross-account parent become invalid and be cleared automatically, or should the backend reject until a valid same-account hierarchy is supplied?
- Does "cross-link classifications are not visible in Projects" refer to list rows being missing, or to labels being normalized to regular labels? This needs one API/browser check before coding.

## Task Order

1. Developer/local setup task: create/sync an ARM Cognito user and record `origin/main` ARM parity.
2. Developer task: restore field-level frontend editability and payload behavior for Admin all-status, PD New-only, and verified ARM parity.
3. Developer task: replace global backend immutability with guarded, Proposal 2-aware update validation.
4. Developer task: verify and fix Projects visibility only after confirming the concrete failure mode.
5. Test-engineer task: add/run focused frontend, backend, and browser smoke validation.
6. Tech-lead review: review changed scope for authorization, hierarchy integrity, and test sufficiency.

## File-Level Tasks

### 1. Frontend Update Step 1 Editability

Files:
- `front-end/src/components/Project/update-project/UpdateProject.tsx`
- `front-end/src/components/Project/create-project/steps/Step1.tsx`
- `front-end/src/components/Project/create-project/steps/Step2.tsx`
- `front-end/src/components/Project/create-project/steps/Step3.tsx`
- `front-end/src/components/Project/create-project/steps/Step4.tsx`
- `front-end/src/components/Project/create-project/steps/Step5.tsx`
- Crediting-period subcomponents used by `Step3`
- `front-end/src/components/Project/create-project/steps/Step1.test.tsx`
- `front-end/src/components/Project/update-project/UpdateProject.test.tsx`

Expected behavior:
- Introduce explicit field capabilities from `UpdateProject.tsx` instead of a single broad switch:
  - `canEditDeveloperAccount`
  - `canEditProjectClassification`
  - `canEditProjectTitle`
  - `canEditParentHierarchy`
- Pass those capabilities into `Step1` instead of making `Step1` infer everything from `isUpdate`.
- Admin updates allow changing Project Developer Account, Project Classification, Project Title, Programme of Activities, and Real Case VPA in every status.
- ARM updates preserve Task 03 main/backend parity; do not narrow or widen ARM access while fixing Admin/PD.
- Owning PD New updates allow changing only Project Classification and Project Title. Project Developer Account, parent hierarchy, Step2, Step3, Step4, Step5, crediting periods, transition fields, dates, fees, notes, locations, SDGs, size, type, and assets remain protected unless the client expands PD scope.
- Owning PD non-New updates remain unavailable or protected.
- Existing cross-link display normalization remains: saved cross-link classifications can render with canonical Real Case/Regular labels in update fields if that is still the accepted requirement.
- `UpdateProject.tsx` must stop overwriting `accountId`, `projectClassificationId`, `name`, `masterProjectId`, and `parentProjectId` with the original project when the user has authority to edit those fields.
- For users without that authority, either keep the overwrite behavior or use a dedicated helper to preserve immutable values deliberately.

Dependencies:
- Existing `AuthContext` role helpers.
- Existing `projectStatusId` mapping where `1` is New. Prefer named lookup/constant if one already exists nearby; do not introduce broad new reference infrastructure for this narrow fix.

Validation criteria:
- Admin update form fields are enabled for New, Listed, and De-certified projects.
- PD New update form allows Project Classification and Project Title but not Project Developer Account.
- PD Listed/non-New update is unavailable or protected.
- ARM local browser behavior matches main/backend parity.
- Submit payload for authorized Admin/ARM updates contains changed account/classification/title/hierarchy values.
- Submit payload for PD New updates contains only changed title/classification plus required unchanged values.
- Submit payload for unauthorized update does not accidentally mutate protected hierarchy values.

### 2. Backend Update Validation

Files:
- `back-end/src/main/java/org/goldstandard/assurance/api/service/ProjectService.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/ProjectAccessPolicy.java` or `PermissionsService.java` only if existing APIs do not expose the needed role/status check
- `back-end/src/test/java/org/goldstandard/assurance/api/service/ProjectServiceTest.java`
- `back-end/src/test/java/org/goldstandard/assurance/api/ProjectApiIntegrationTest.java`

Expected behavior:
- Remove or narrow the global `validateUpdateKeepsAccountAndHierarchy` rule.
- Keep protection for users/projects that should not edit identity/hierarchy.
- Allow admin updates to account/classification/title/master/parent fields in every status.
- Preserve Task 03 ARM update behavior and lock it with tests.
- Allow owning PD updates to title/classification only while persisted status is New; keep account reassignment and all other `ProjectUpdateDTO` fields blocked/preserved.
- When hierarchy-affecting fields change, validate the requested target hierarchy using the same existing `CandidateParentEligibilityPolicy.validateParentEligibility(...)` semantics used by create.
- Effective account for validation must be the requested account when admin changes account, not necessarily the original account.
- If classification does not require parents, update should clear or accept null parent fields consistently with create/update form behavior.
- Cross-account parents remain allowed only for cross-link classifications. Regular classifications must still reject invalid cross-account hierarchy.

Dependencies:
- Existing `ProjectAccessPolicy.validateCanUpdateProject(...)` for base authorization.
- Existing `CandidateParentEligibilityPolicy` and `findProjectIfPresent(...)`.
- Existing `ProjectUpdateApplier` behavior for applying account/classification/hierarchy fields.

Validation criteria:
- Admin can update New, Listed, and De-certified project account/classification/title/hierarchy when candidate-parent rules pass.
- ARM update behavior remains unchanged and is covered by tests.
- Owning PD can update Project Title and Project Classification while the project is New.
- Owning PD cannot update Project Developer Account.
- Owning PD cannot mutate description, location, project type, dates, fee schedule, notes, crediting periods, transition details, parent hierarchy, SDGs, size, assets, or other non-title/classification fields through a tampered payload.
- Owning PD cannot update title/classification once the project is Listed or later.
- Non-admin/project-developer cannot change account/hierarchy beyond current allowed behavior.
- Admin cannot create an invalid hierarchy by update, for example regular VPA linked to a cross-account POA unless allowed by classification semantics.
- Existing external update and audit behavior still runs after valid updates.

### 3. Projects Visibility Diagnosis and Fix

Files to inspect first:
- `front-end/src/components/Project/project-list/ProjectList.tsx`
- `front-end/src/components/Project/project-list/ProjectTreeList.tsx`
- `front-end/src/components/Project/Project.tsx`
- `front-end/src/common/data-types/DataReferenceTypes.tsx`
- `back-end/src/main/java/org/goldstandard/assurance/api/controller/ProjectController.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/ProjectFinder.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/ProjectSearchSpecFactory.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/entity/view/SearchableProjectView.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/entity/view/ProjectTreeNodeView.java`
- `data-migration/migrations/002190-add-searchable-projects-view.sql`
- `data-migration/migrations/002260-add-project-tree-node-view.sql`
- `data-migration/migrations/003680-add-cross-linking-project-classifications.sql`

Expected behavior:
- Admin `/projects` should not hide projects merely because their classification reference data is inactive.
- If the API already returns the rows, fix any frontend label/filter confusion rather than changing backend search.
- If `/projects` API omits rows, fix `ProjectFinder`/search view/spec or seed/test data issue, not the classification active flag by default.
- If only labels are normalized, document/confirm whether Projects should show canonical labels or raw cross-link labels. Do not silently reverse the Proposal 2 normalization decision globally.
- Keep cross-link create options available via `getCustomReferenceOptions("ProjectClassification", currentValue)`.

Dependencies:
- Existing backend views currently select from `gs.project` without joining `project_classification.is_active`; verify live DB view definitions match the migrations.
- Existing frontend admin route uses `ProjectList`; `ProjectTreeList` is for `/project-trees` and project-developer role.

Validation criteria:
- Admin API call to `/projects` returns cross-link projects by ID/GSID.
- Admin UI `/projects` shows cross-link projects in the list/search result.
- Project-developer list/tree access remains restricted by existing access policy.
- Classification display is consistent with the agreed product semantics.
- Dashboard/report/export label normalization is not changed by a Projects-list visibility fix.

## Specific Tests to Add or Update

Frontend:
- `Step1.test.tsx`: add/update tests proving Step 1 update fields are enabled for admin all-status updates when capability props are true.
- `Step1.test.tsx`: add/update tests proving the same fields remain disabled/read-only for unauthorized update contexts.
- `UpdateProject.test.tsx`: assert admin all-status submit payload preserves changed `accountId`, `projectClassificationId`, `name`, `masterProjectId`, and `parentProjectId` instead of overwriting from the original project.
- `UpdateProject.test.tsx`: assert PD New submit only allows title/classification changes and preserves original protected fields.
- If Projects visibility is frontend-side, create `ProjectList.test.tsx` that renders/fetches a cross-link-classified project row and does not filter it out because the classification reference is inactive.

Backend:
- `ProjectServiceTest`: replace `updateProjectRejectsAccountChangeBeforeApplyingUpdates` and `updateProjectRejectsHierarchyChangeBeforeApplyingUpdates` with role/status-aware tests.
- `ProjectServiceTest`: add admin all-status success cases where account/classification/master/parent changes are passed to `ProjectUpdateApplier` after candidate-parent validation.
- `ProjectServiceTest`: add invalid hierarchy rejection case that verifies `updateApplier` and transaction are not called.
- `ProjectApiIntegrationTest`: add/update integration cases where admin updates New, Listed, and De-certified project hierarchy successfully.
- `ProjectApiIntegrationTest`: add/update negative case for project-developer or non-New project changing protected hierarchy fields.
- `ProjectApiIntegrationTest`: add tampered-payload case proving owning PD New cannot change `accountId` or non-title/classification details.
- If API list visibility is broken, add integration coverage that `/projects` returns projects with `REAL_CASE_VPA_CROSS_LINKAGE` and `REGULAR_VPA_CROSS_LINKAGE` for admin.

Suggested commands:
- Frontend focused: `cd front-end && $HOME/.nvm/versions/node/v20.19.0/bin/node ./node_modules/jest/bin/jest.js --runTestsByPath src/components/Project/create-project/steps/Step1.test.tsx src/components/Project/update-project/UpdateProject.test.tsx --coverage=false --maxWorkers=50%`
- Frontend list if touched: `cd front-end && $HOME/.nvm/versions/node/v20.19.0/bin/node ./node_modules/jest/bin/jest.js --runTestsByPath src/components/Project/project-list/ProjectList.test.tsx --coverage=false --maxWorkers=50%`
- Backend focused: `cd back-end && /opt/homebrew/bin/mvn -Dtest=ProjectServiceTest,ProjectApiIntegrationTest test`
- Dashboard/report/export focused: `cd back-end && /opt/homebrew/bin/mvn -Dtest=CommonDashboardConverterTest,DashboardRequestSpecFactoryTest,CommonDashboardReportRowPopulatorTest,StatusReportApiIntegrationTest test`

## Risks and Tradeoffs

- Admin all-status edit parity is explicitly in scope because current `main` allows it. The control is backend hierarchy validation, audit coverage, and regression tests for New plus non-New statuses; do not status-gate Admin edits to New.
- Frontend enabling alone is unsafe; backend must own the final authorization and hierarchy validation.
- Removing `validateUpdateKeepsAccountAndHierarchy` without replacement would re-open invalid cross-account hierarchy updates. The replacement should validate only when protected fields change, and use `CandidateParentEligibilityPolicy`.
- If admins can change `accountId`, the candidate parent option loaders must refetch against the new account and clear stale parent fields when account/classification changes. Existing `Step1` clear logic mostly handles this; tests should lock it.
- Keeping cross-link classifications inactive is reasonable for "special create option" semantics, but any UI using only `activeReferenceTypes.ProjectClassification` will miss them. Prefer narrow helper usage over making the reference rows globally active.
- Normalizing display labels preserves Proposal 2 semantics but can make admins think cross-link classifications are missing. If raw labels are required on `/projects`, implement it only for that surface.
- No new packages are required. Dependency/license risk is therefore low for this plan. Existing branch package changes are outside this regression fix scope and should be reviewed separately if they are part of the release.
