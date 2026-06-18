# Proposal 2 Regression Fix Plan

Date: 2026-06-18
Branch: `feat/proposal-2-cross-linking`
Scope: Planning only. No implementation in this pass.

## Parity And Regression Findings

Sources reviewed:
- Proposal 2 PDF: `/Users/patslaurel/Downloads/AM-Cross linking regular case VPAs with different PDs-090626-025419.pdf`
- Gmail thread: `Thinking Machines Test Feature: Cross-Linking VPAs for Assurance`
- Local demo summary from 2026-06-18
- Six-agent paired code review across admin editability, project visibility, and broader Proposal 2 regression surface
- Branch comparison: `d37fc36..HEAD` and `origin/main..HEAD` both show the same focused Proposal 2 delta; local `main` is stale at `7a17219` and includes unrelated extraction/local-dev noise.

Bottom line:
- Proposal 2 create behavior is mostly aligned with the email/PDF: new cross-link labels exist, PD users can create cross-link VPAs, Regular Case cross-link requires both PoA and Real Case VPA selection, all PoAs/Real Case VPAs under the chosen parent are intended to be available, and linked parent PDs must not gain child visibility.
- We introduced at least one release-blocking parity regression: admin/ARM correction of New-project account/classification/hierarchy fields is blocked in the UI, stripped from the submit payload, and rejected by the backend.
- The reported "admin cannot see the new project classifications in Projects" is not currently proven as a missing-row API bug. It is a visibility/label gap: `/projects` can return rows with raw classification IDs `10/11`, but the Projects table has no classification column and other surfaces intentionally normalize cross-link labels to `Real Case VPA` / `Regular VPA`.

| Area | Source Requirement Or Baseline | Current Branch Behavior | Finding | Severity | Required Action |
|---|---|---|---|---|---|
| Admin/ARM New-project edit | Staging feedback says admin can edit VPA identity/linkage while status is `New`. Email from Annyta says to "enable edit access for ARM accounts." PDF says Update Project should not need extra changes because it reuses wizard steps. | `Step1` disables Project Developer Account, Project Classification, Programme of Activities, and Real Case VPA whenever `isUpdate` is true. `UpdateProject` overwrites submitted account/classification/master/parent values with originals. `ProjectService` rejects those changes for every role/status. | Confirmed regression. | P1 | Restore role/status-aware edit for `GS_ADMIN` and `ARM_TEAM` on `NEW` projects; backend must validate changed hierarchy instead of globally rejecting it. |
| Project Developer edit after creation | Meerim's 2026-06-18 follow-up asks whether PD can edit classification/linked PoA after creation and in which statuses. | PD update flow is locked for these fields. | Open product question, not confirmed regression. | P2 | Keep PD protected until Anita confirms otherwise. Add this as a client clarification question. |
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
| Admin/ARM update New VPA | Admin/ARM can edit account/classification/linked PoA/linked Real Case VPA when status is `New`; invalid hierarchy is rejected. | Failing now. | P1 |
| PD update New VPA linkage/account | PD cannot reassign account/hierarchy unless client explicitly approves. | Protected now. | P2 |
| Dashboard/report/export labels | Cross-link rows roll up as normal Real/Regular labels; PD/account dimension remains clear. | Backend tests cover parts; browser/export evidence still incomplete. | P2 |
| Candidate-parent details | Cross-account parent details are either accepted as selection/registry data or intentionally minimized. | Open decision. | P2 |

## Assumptions

- "Admin users should be able to edit everything on a New project" means Step 1 account, classification, POA, and Real Case VPA fields should be editable for `GS_ADMIN` and likely `ARM_TEAM` when the project status is `NEW`.
- Non-admin project-developer users should not gain broader cross-account edit capability from this fix.
- Cross-link classifications remain inactive reference data to avoid making them ordinary active classifications everywhere.
- Cross-link create options should stay available where Proposal 2 intended them, but display/detail surfaces may continue normalizing them to Real Case VPA or Regular VPA.
- The reported "not visible in Projects" issue could mean either cross-link project rows are absent from `/projects`, or the cross-link classification labels are normalized and therefore not visibly distinct. The implementation must verify which symptom is real before changing data semantics.

## Open Questions

- Should `ARM_TEAM` have the same New-project Step 1 edit power as admin, or is this specifically `GS_ADMIN` only?
- Should admins be allowed to change hierarchy/classification only while status is `NEW`, or also after `LISTED`/certified states? The safer plan is `NEW` only.
- If an admin changes a cross-link project classification to a non-cross-link classification, should the previous cross-account parent become invalid and be cleared automatically, or should the backend reject until a valid same-account hierarchy is supplied?
- Does "cross-link classifications are not visible in Projects" refer to list rows being missing, or to labels being normalized to regular labels? This needs one API/browser check before coding.

## Task Order

1. Developer task: restore role/status-aware frontend editability and payload behavior.
2. Developer task: replace global backend immutability with guarded, Proposal 2-aware update validation.
3. Developer task: verify and fix Projects visibility only after confirming the concrete failure mode.
4. Test-engineer task: add/run focused frontend, backend, and one browser smoke validation.
5. Tech-lead review: review changed scope for authorization, hierarchy integrity, and test sufficiency.

## File-Level Tasks

### 1. Frontend Update Step 1 Editability

Files:
- `front-end/src/components/Project/update-project/UpdateProject.tsx`
- `front-end/src/components/Project/create-project/steps/Step1.tsx`
- `front-end/src/components/Project/create-project/steps/Step1.test.tsx`
- `front-end/src/components/Project/update-project/UpdateProject.test.tsx`

Expected behavior:
- Introduce an explicit capability from `UpdateProject.tsx`, such as `canEditProjectIdentityAndHierarchy`, based on authenticated role and `projectStatusId === NEW`.
- Pass that capability into `Step1` instead of making `Step1` infer everything from `isUpdate`.
- `Step1` keeps fields disabled for update flows unless that explicit capability is true.
- Admin New project updates allow changing:
  - Project Developer Account
  - Project Classification
  - Programme of Activities
  - Real Case VPA
- Existing cross-link display normalization remains: saved cross-link classifications can render with canonical Real Case/Regular labels in update fields if that is still the accepted requirement.
- `UpdateProject.tsx` must stop overwriting `accountId`, `projectClassificationId`, `masterProjectId`, and `parentProjectId` with the original project when the user has authority to edit those fields.
- For users without that authority, either keep the overwrite behavior or use a dedicated helper to preserve immutable values deliberately.

Dependencies:
- Existing `AuthContext` role helpers.
- Existing `projectStatusId` mapping where `1` is New. Prefer named lookup/constant if one already exists nearby; do not introduce broad new reference infrastructure for this narrow fix.

Validation criteria:
- Admin New update form fields are enabled.
- Non-admin update flow still does not expose unauthorized identity/hierarchy edits.
- Submit payload for admin New update contains the changed account/classification/hierarchy values.
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
- Allow admin New-project updates to account/classification/master/parent fields.
- When hierarchy-affecting fields change, validate the requested target hierarchy using the same existing `CandidateParentEligibilityPolicy.validateParentEligibility(...)` semantics used by create.
- Effective account for validation must be the requested account when admin changes account, not necessarily the original account.
- If classification does not require parents, update should clear or accept null parent fields consistently with create/update form behavior.
- Cross-account parents remain allowed only for cross-link classifications. Regular classifications must still reject invalid cross-account hierarchy.

Dependencies:
- Existing `ProjectAccessPolicy.validateCanUpdateProject(...)` for base authorization.
- Existing `CandidateParentEligibilityPolicy` and `findProjectIfPresent(...)`.
- Existing `ProjectUpdateApplier` behavior for applying account/classification/hierarchy fields.

Validation criteria:
- Admin can update New project account/classification/hierarchy when candidate-parent rules pass.
- Non-admin/project-developer cannot change account/classification/hierarchy beyond current allowed behavior.
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

## Specific Tests to Add or Update

Frontend:
- `Step1.test.tsx`: add/update tests proving Step 1 update fields are enabled for admin New updates when the new capability prop is true.
- `Step1.test.tsx`: add/update tests proving the same fields remain disabled/read-only for unauthorized update contexts.
- `UpdateProject.test.tsx`: assert admin New submit payload preserves changed `accountId`, `projectClassificationId`, `masterProjectId`, and `parentProjectId` instead of overwriting from the original project.
- `UpdateProject.test.tsx`: assert non-authorized submit payload preserves original immutable fields if that behavior remains.
- If Projects visibility is frontend-side, add `ProjectList` test that renders/fetches a cross-link-classified project row and does not filter it out because the classification reference is inactive.

Backend:
- `ProjectServiceTest`: replace `updateProjectRejectsAccountChangeBeforeApplyingUpdates` and `updateProjectRejectsHierarchyChangeBeforeApplyingUpdates` with role/status-aware tests.
- `ProjectServiceTest`: add admin/New success case where account/classification/master/parent changes are passed to `ProjectUpdateApplier` after candidate-parent validation.
- `ProjectServiceTest`: add invalid hierarchy rejection case that verifies `updateApplier` and transaction are not called.
- `ProjectApiIntegrationTest`: add/update an integration case where admin updates a New project hierarchy successfully.
- `ProjectApiIntegrationTest`: add/update negative case for project-developer or non-New project changing protected hierarchy fields.
- If API list visibility is broken, add integration coverage that `/projects` returns projects with `REAL_CASE_VPA_CROSS_LINKAGE` and `REGULAR_VPA_CROSS_LINKAGE` for admin.

Suggested commands:
- Frontend focused: `cd front-end && npm test -- --runTestsByPath src/components/Project/create-project/steps/Step1.test.tsx src/components/Project/update-project/UpdateProject.test.tsx --coverage=false`
- Frontend list if touched: `cd front-end && npm test -- --runTestsByPath src/components/Project/project-list/ProjectList.test.tsx --coverage=false`
- Backend focused: `cd back-end && /opt/homebrew/bin/mvn -Dtest=ProjectServiceTest,ProjectApiIntegrationTest test`

## Risks and Tradeoffs

- Allowing admin hierarchy edits is correct for New projects but risky after requests, crediting periods, bundles, invoices, or external-system sync have been created. Keep the first fix status-gated to New unless product explicitly approves broader edits.
- Frontend enabling alone is unsafe; backend must own the final authorization and hierarchy validation.
- Removing `validateUpdateKeepsAccountAndHierarchy` without replacement would re-open invalid cross-account hierarchy updates. The replacement should validate only when protected fields change, and use `CandidateParentEligibilityPolicy`.
- If admins can change `accountId`, the candidate parent option loaders must refetch against the new account and clear stale parent fields when account/classification changes. Existing `Step1` clear logic mostly handles this; tests should lock it.
- Keeping cross-link classifications inactive is reasonable for "special create option" semantics, but any UI using only `activeReferenceTypes.ProjectClassification` will miss them. Prefer narrow helper usage over making the reference rows globally active.
- Normalizing display labels preserves Proposal 2 semantics but can make admins think cross-link classifications are missing. If raw labels are required on `/projects`, implement it only for that surface.
- No new packages are required. Dependency/license risk is therefore low for this plan. Existing branch package changes are outside this regression fix scope and should be reviewed separately if they are part of the release.
