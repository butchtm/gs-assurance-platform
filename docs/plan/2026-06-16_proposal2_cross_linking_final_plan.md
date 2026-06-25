# Proposal 2 Cross-Linking Final Implementation Plan

Date: 2026-06-16
Owner role: tech-lead
Status: reviewed final plan
Reviewed by: tech-lead, solutions-architect, tech-lead

## 1. Scope

Implement Proposal 2 for VPA cross-linking in the Assurance platform.

The first version lets Project Developer users create two new VPA classifications that link to existing Programme of Activities and Real Case VPAs across Project Developer accounts, while keeping created projects owned by the creating Project Developer account.

Planned user-facing create options:

- `Real Case VPA (cross linkage)`
- `Regular Case VPA (cross linkage)`

Internal codes:

- `REAL_CASE_VPA_CROSS_LINKAGE`
- `REGULAR_VPA_CROSS_LINKAGE`

Current working tree note: partial Task 1 and Task 2 artifacts already exist for IDs `10` and `11` in the migration, test reference data, and `ProjectClassification`. Treat those as started work to verify and complete, not as fully implemented behavior.
Task 1A seeds IDs `10` and `11` as inactive so current create flows do not expose unsupported options before Task 2+ backend semantics and Task 7 frontend behavior land.

## 2. Confirmed Decisions

- Project Developer users can select and create both cross-link classifications after the Task 2+ backend semantics and Task 7 create-flow work lands.
- ARM accounts must have edit access to cross-link projects.
- Cross-link Real Case VPA requires a target PoA only.
- Cross-link Regular Case VPA requires a target PoA plus a Real Case VPA under that selected PoA.
- PoA candidates include all PoAs across all statuses, including New, Listed, and Design Certified.
- Real Case VPA candidates include all Real Case VPAs under the selected PoA, across statuses.
- Cross-link classifications inherit existing request and batching behavior from their matching VPA type.
- Existing Real Case VPA and Regular Case VPA flows remain unchanged.
- Dashboards, reports, exports, request batching, and project tree surfaces should not introduce confusing new client-facing type terms. They should roll up/display cross-link Real Case as `Real Case VPA` and cross-link Regular as `Regular Case VPA`, with PD/account name clearly visible where those surfaces already show owner context.
- Linked PoA or linked Real Case VPA owner accounts do not gain visibility or action access to the newly created linked project.
- No cleanup or migration of existing dummy/workaround projects is part of this first version.
- Paris Agreement Alignment work is out of scope.
- No new runtime dependency is expected for this scope.

## 3. Non-Goals

- No new hierarchy table or cross-link table.
- No broadening of `GET /projects/{id}` for arbitrary cross-account project reads.
- No automatic cleanup of dummy PoAs or dummy Real Case VPAs.
- No Cypress or full browser automation gate for the first demo.
- No pagination/search redesign for large cross-account candidate lists in the first version.
- No changes to PAA tagging, methodology PAA flags, or PAA requests.
- No changes that make existing Real/Regular VPA candidate selection cross-account.

## 4. Architecture And Behavior Rules

### 4.1 Data Model

Keep using `gs.project` with existing hierarchy fields:

| Scenario | Created project owner | `master_project_id` | `parent_project_id` |
| --- | --- | --- | --- |
| Cross-link Real Case VPA | creating PD account | selected PoA | selected PoA |
| Cross-link Regular Case VPA | creating PD account | selected PoA | selected Real Case VPA |

Add lookup/reference rows and compatibility mapping rows only. Do not add a new hierarchy model.

### 4.2 Classification Semantics

Treat the new classifications as specialized create-time options.

Runtime grouping:

- `REAL_CASE_VPA_CROSS_LINKAGE` behaves as a Real Case VPA.
- `REGULAR_VPA_CROSS_LINKAGE` behaves as a Regular Case VPA.

Add one canonical backend classification semantics layer instead of scattering conditional display fixes. It should expose:

- persisted classification ID/code
- canonical VPA kind
- create selector label
- normalized display label
- external/registry/SDG/finance mapping value
- request/batch lookup classification ID or equivalent bucket
- dashboard/report filter expansion IDs

Create flow labels may show the cross-link names. Reporting, dashboards, exports, request batching, and tree/list labels should use normal Real Case VPA / Regular Case VPA labels unless a surface explicitly needs the create-time classification for debugging or admin support.

### 4.3 Candidate Selection

Existing classifications stay account-scoped for Project Developer users.

Cross-link classifications use broader candidate rules:

- Cross-link Real Case VPA:
  - PoA candidates are all PoAs across accounts and statuses.
  - Real Case selector is hidden.
- Cross-link Regular Case VPA:
  - PoA candidates are all PoAs across accounts and statuses.
  - Real Case candidates are all Real Case VPAs under the selected PoA.
  - Real Case candidates are not shown before a PoA is selected.

First-version candidate-list guardrails:

- use minimal projection for candidate lists
- apply deterministic sorting
- verify query paths use existing or suitable indexes for classification, master project, account, and status
- add a bounded large-fixture check if local candidate count makes selectors slow

### 4.4 Candidate Detail Access

Add a candidate-scoped detail path instead of widening normal project detail access.

Expected endpoint:

- `POST /projects/candidate-parent-details`

Request fields:

- `accountId`
- `classificationId`
- `candidateProjectId`
- optional `masterProjectId`

Interpretation:

- Cross-link Real: `candidateProjectId` is the selected PoA.
- Cross-link Regular PoA hydration: `candidateProjectId` is the selected PoA.
- Cross-link Regular Real Case hydration and methodology filtering: `candidateProjectId` is the selected Real Case and `masterProjectId` is the selected PoA.

Response fields should be exact and whitelisted. Include only the fields needed for create-wizard inheritance and methodology filtering:

- `id`
- `gsid`
- `name`
- classification reference
- status reference
- project start date
- carbon stream
- project size
- project types
- locations/countries
- assets
- SDGs
- methodologies and crediting periods

Exclude broad project details, including:

- notes
- PII metadata
- risk summary
- registry URL
- current request
- fee schedule
- estimated annual units
- bundle details
- account-sensitive fields not required by the create wizard
- raw `ProjectDTO`

The create wizard must not call normal `GET /projects/{id}` for selected cross-account parent candidates.

### 4.5 Authorization And Visibility

Project Developer account resolution:

- create uses a server-resolved effective owner account before persistence
- matching account can be accepted
- mismatched account is rejected
- do not trust PD-supplied `accountId`

Admin/ARM:

- can use existing account-selection behavior where already supported
- can edit cross-link projects under the same permission model used for corresponding VPA projects

Linked parent account visibility:

- owning a selected PoA or selected Real Case VPA does not grant visibility or action access to the new cross-linked child project
- tree/list APIs must avoid exposing unrelated sibling projects from the linked parent account
- for cross-linked projects, tree output should be a pruned authorized lineage: selected PoA, selected Real Case if applicable, and the visible/owned created child only

### 4.6 Validation Matrix

| Classification | Required relationship | Allowed parent account |
| --- | --- | --- |
| Existing Real Case VPA | PoA selected; `masterProjectId = parentProjectId` | existing account-scoped behavior |
| Existing Regular Case VPA | PoA plus Real Case under selected PoA | existing account-scoped behavior |
| Cross-link Real Case VPA | PoA selected; `masterProjectId = parentProjectId` | any account, if candidate is a PoA |
| Cross-link Regular Case VPA | PoA plus Real Case under selected PoA | any account, if Real Case belongs to selected PoA |

Reject invalid combinations server-side even if the frontend hides them.

For the first version, reject updates that change `accountId`, `projectClassificationId`, `masterProjectId`, or `parentProjectId` unless the same shared eligibility/account policy fully validates the requested change before persistence. The implementation should choose and test one explicit behavior; do not leave update mutation behavior implicit.

## 5. Task Breakdown

### Task 1A: Reference Data, Batch Mapping, And Started Artifact Verification

Files:

- `data-migration/migrations/changelog.yaml`
- `data-migration/migrations/003680-add-cross-linking-project-classifications.sql`
- `back-end/src/test/resources/database/reference-data.sql`
- any production/test migration or reference data file that seeds `gs.batch_mapping`

Work:

- verify/complete the two project classification lookup rows, seeded inactive until Task 2+ create-flow support lands
- verify IDs `10` and `11` are unused in production migration history, or adjust constants/test data together
- add `gs.batch_mapping` rows for `REAL_CASE_VPA_CROSS_LINKAGE` and `REGULAR_VPA_CROSS_LINKAGE` mirroring the current `REAL_CASE_VPA` and `REGULAR_VPA` mappings, or change `BatchTypeService` to query an effective classification bucket before lookup
- keep production migration and test reference data aligned

Acceptance:

- Liquibase applies locally
- tests can resolve both classifications by ID/code
- batch type lookup and batch candidate-project lookup for both cross-link classifications return the same allowed behavior as their matching VPA type

### Task 1B: Backend Integration And Demo Fixtures

Files:

- `back-end/src/test/java/org/goldstandard/assurance/api/populator/AccountPopulator.java`
- `back-end/src/test/java/org/goldstandard/assurance/api/populator/UserPopulator.java`
- `back-end/src/test/java/org/goldstandard/assurance/api/populator/ProjectPopulator.java`
- new `back-end/src/test/java/org/goldstandard/assurance/api/populator/CrossLinkingProjectFixture.java` if cleaner
- `back-end/local/seed-cross-linking-fixtures.sql`

Work:

- create deterministic Java integration-test fixtures for Alpha/Beta cross-account tests
- keep local SQL seed idempotent for browser demos
- create Alpha and Beta PD accounts/users
- create Beta PoAs across required statuses
- create a Beta PoA with no Real Case
- create at least two Beta Real Cases with distinct methodology data
- clearly mark or exclude any legacy workaround-style local rows using normal classification IDs

Acceptance:

- integration fixtures create the Alpha/Beta matrix without depending on local SQL seed
- local seed can run twice without duplicate rows
- demo can be run from a clean local database after migrations

### Task 2: Backend Classification Helpers, Semantics, And Display Mapping

Files:

- `back-end/src/main/java/org/goldstandard/assurance/api/entity/lookup/ProjectClassification.java`
- new or existing backend classification semantics mapper
- `back-end/src/main/java/org/goldstandard/assurance/api/converter/ProjectClassificationConverter.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/converter/CommonDashboardConverter.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/report/CommonDashboardReportRowPopulator.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/external/ExternalProjectClassificationConverter.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/external/registry/RegistryProjectClassificationConverter.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/external/sdg/SdgProjectClassificationConverter.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/finance/FinanceProjectClassificationFactory.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/BatchTypeService.java` if using effective bucket lookup instead of mapping rows
- related unit tests

Work:

- verify/complete constants and helper methods for both cross-link classifications
- include cross-link Real Case in Real Case helper buckets
- include cross-link Regular in Regular Case helper buckets
- add canonical classification semantics for display, external mapping, filter expansion, and batch/request compatibility
- normalize non-create-selector client-facing surfaces so `(cross linkage)` does not leak into dashboards, exports, reports, batch summaries, external payloads, or tree/list labels

Acceptance:

- helper tests cover all Real Case, Regular Case, and cross-link groupings
- Real Case filters include native Real Case plus cross-link Real Case
- Regular filters include native Regular plus cross-link Regular
- external/registry/SDG/finance mappings do not receive unknown cross-link codes unless intentionally supported
- no non-create-selector client-facing surface displays `(cross linkage)`
- dashboard DTO, dashboard export, external/registry/SDG/finance mapping, batch DTOs, and tree/list label tests cover normalization

### Task 3: Shared Eligibility And Account Policy

Files:

- new `back-end/src/main/java/org/goldstandard/assurance/api/service/EffectiveProjectAccountResolver.java`
- new `back-end/src/main/java/org/goldstandard/assurance/api/service/CandidateParentEligibilityPolicy.java` or equivalent
- focused unit tests for both classes

Work:

- centralize effective account resolution
- centralize parent eligibility validation
- reuse these rules from candidate list, candidate detail, create, and update paths

Acceptance:

- PD account spoofing is rejected
- Admin/ARM account behavior matches existing account-selection permissions
- existing classifications remain account-scoped
- cross-link classifications allow valid cross-account parent paths
- invalid PoA/Real Case pairings are rejected

### Task 4: Backend Candidate APIs

Files:

- `back-end/src/main/java/org/goldstandard/assurance/api/controller/ProjectController.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/CandidateParentProjectFinder.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/repository/ProjectRepository.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/dto/GetCandidatePOAsRequest.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/dto/GetCandidateRealCaseVPAsRequest.java`
- new `back-end/src/main/java/org/goldstandard/assurance/api/dto/GetCandidateParentProjectDetailsRequest.java`
- new `back-end/src/main/java/org/goldstandard/assurance/api/dto/CandidateParentProjectDetailsDTO.java`
- new dedicated converter or `ProjectConverter.toCandidateParentDetailsDto`
- focused API integration tests

Work:

- update candidate PoA endpoint to use classification-aware cross-link eligibility
- add `classificationId` to Real Case candidate requests
- update candidate Real Case endpoint to use selected PoA plus classification context
- add candidate parent detail endpoint with exact whitelisted response data from section 4.4
- keep normal project detail authorization unchanged
- apply minimal projection, deterministic sort, and query/index checks for broad candidate lists

Acceptance:

- existing Regular VPA as PD Alpha does not return PD Beta candidates
- cross-link Real/Regular as PD Alpha returns PD Beta PoAs across statuses
- cross-link Regular returns only Real Case VPAs under the selected PoA
- candidate detail succeeds for valid cross-link candidates
- invalid candidate, mismatched PoA/Real Case, spoofed account, and forbidden fields are covered by tests
- normal `GET /projects/{id}` remains denied where it was previously denied

### Task 5: Backend Create/Update Validation

Files:

- `back-end/src/main/java/org/goldstandard/assurance/api/service/ProjectService.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/ProjectUpdateApplier.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/dto/ProjectCreateDTO.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/dto/ProjectUpdateDTO.java`
- focused hierarchy validation integration tests

Work:

- apply shared eligibility validation before persisting hierarchy/classification values
- resolve and persist the effective owner account server-side for create
- reject PD account spoofing before persistence
- implement the chosen update policy from section 4.6
- preserve existing Real/Regular create/update behavior

Acceptance:

- cross-link Real create persists selected PoA as both master and parent
- cross-link Regular create persists selected PoA as master and selected Real Case as parent
- owner account remains the creating/effective PD account
- invalid hierarchy combinations fail before persistence
- update attempts that change account, classification, master, or parent are rejected, or supported only with explicit policy validation tests for PD, Admin, and ARM paths
- existing project creation tests still pass

### Task 6: Frontend Classification Helpers, Schema, And API Contracts

Files:

- new `front-end/src/common/project/projectClassification.ts`
- `front-end/src/common/project/Project.tsx`
- `front-end/src/common/data-types/DebugReferenceTypes.tsx`
- `front-end/src/components/Project/ProjectFormValidationSchema.tsx`
- `front-end/src/components/Project/create-project/CreateProject.tsx`
- focused frontend tests

Work:

- centralize frontend classification helper logic
- add cross-link classifications to debug reference data
- add candidate parent detail API wrapper with typed request/response
- update create-form validation for cross-link required fields
- keep submit payload owner/account semantics from being overwritten by selected parent detail

Acceptance:

- validation requires PoA for cross-link Real
- validation requires PoA and Real Case for cross-link Regular
- owner account in submit payload remains the creating/effective account
- helper tests cover new and existing classifications

### Task 7: Frontend Create Wizard Flow

Files:

- `front-end/src/components/Project/create-project/steps/Step1.tsx`
- `front-end/src/components/Project/create-project/steps/Step2.tsx`
- `front-end/src/components/Project/create-project/steps/Step3.tsx`
- `front-end/src/components/Project/create-project/steps/Step5.tsx`
- `front-end/src/components/Project/create-project/steps/ReviewStep.tsx`
- `front-end/src/components/Project/create-project/steps/ProjectTitle.tsx`
- focused step tests

Work:

- show cross-link classification options in Create Project
- for cross-link Real, show PoA selector and hide Real Case selector
- for cross-link Regular, show PoA selector then Real Case selector under selected PoA
- replace normal `GET /projects/{id}` parent hydration with candidate detail for selected cross-account parent candidates
- hydrate inherited fields from candidate parent detail
- reset stale selected PoA, Real Case, inherited fields, methodologies, and warnings when account/classification/PoA/Real Case changes
- ignore stale async responses if the selected account/classification/parent changed after request start

Acceptance:

- Step1 field visibility matches all four VPA paths
- changing classification or PoA clears stale child selections
- methodology filtering follows the selected Real Case for cross-link Regular
- review step shows correct selected ancestry

### Task 8: Authorized Tree/List, Dashboard, Export, And Reporting Surfaces

Files:

- `back-end/src/main/java/org/goldstandard/assurance/api/service/ProjectTreeFactory.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/ProjectService.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/ProjectFinder.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/ProjectSearchSpecFactory.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/entity/view/ProjectTreeNodeView.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/entity/ProjectTreeNode.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/dto/ProjectTreeNodeDTO.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/converter/CommonDashboardConverter.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/DashboardRequestSpecFactory.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/report/CommonDashboardReportRowPopulator.java`
- `back-end/src/test/resources/database/h2_compatibility.sql` if view compatibility changes
- `front-end/src/components/requests/dashboard/CommonDashboardTable.tsx`
- `front-end/src/components/Project/view-project/ViewProjectHierarchyRowLabel.tsx`
- `front-end/src/components/Project/view-project/ViewProjectHierarchyTree.test.tsx`

Work:

- implement pruned authorized lineage/tree behavior for cross-linked projects
- verify project list shows created cross-link projects to the creating PD account
- verify linked parent account users do not see or act on the new child project
- verify tree renders selected ancestry without unrelated linked-account siblings
- normalize dashboard, report, export, tree, and list labels to normal VPA labels where required
- add account/PD name to tree nodes if needed so hierarchy rows clearly show ownership

Acceptance:

- PD Alpha sees Alpha-owned cross-link projects
- PD Beta does not gain visibility to Alpha-created projects merely because they link to Beta PoA/Real Case
- selected ancestry renders for the creating PD without unrelated Beta siblings
- Admin/ARM sees expected edit path
- dashboard/report/export/tree/list labels roll up as normal Real Case VPA or Regular Case VPA where required

### Task 9: Demo Readiness And Runbook

Files:

- `back-end/local/seed-cross-linking-fixtures.sql`
- `docs/mops/features/proposal2-cross-linking-demo.md` or a temporary plan-runbook file if `docs/mops` is not yet part of this repo

Work:

- document setup, seed, test, browser demo, evidence, and cleanup steps
- include local prerequisites and stop conditions
- avoid hardcoded container names in runbook commands where Compose service names work

Demo readiness gate:

- `data-migration` exists in the expected sibling path used by `back-end/local/compose.yml`
- migrations apply successfully
- classification rows and batch mapping rows are present
- local seed is idempotent
- local auth users exist or the test-auth path is documented
- frontend uses `VITE_PROXY_TARGET=http://localhost:8080`
- local DB port/Compose override expectations are documented

Acceptance:

- demo can be run from a clean local database after migrations
- runbook has enough detail for another developer to reproduce the demo
- demo commands avoid depending on a single hardcoded container name when a Compose service command is available

## 6. Validation Plan

Backend unit tests:

```bash
cd back-end
mvn test -Dtest=ProjectClassificationTest,EffectiveProjectAccountResolverTest,CandidateParentEligibilityPolicyTest,CandidateParentProjectFinderTest
```

Backend integration tests:

```bash
cd back-end
mvn test-compile failsafe:integration-test failsafe:verify -Dskip.integration.tests=false -Dit.test=CandidateParentProjectApiIntegrationTest,CandidateParentProjectDetailsApiIntegrationTest,ProjectHierarchyValidationApiIntegrationTest,ProjectTreeAuthorizationApiIntegrationTest,BatchMappingCrossLinkingApiIntegrationTest
```

Frontend focused tests:

```bash
cd front-end
npm test -- --runTestsByPath \
  src/common/project/projectClassification.test.ts \
  src/components/Project/ProjectFormValidationSchema.test.tsx \
  src/components/Project/create-project/steps/Step1.test.tsx \
  src/components/Project/create-project/steps/Step2.test.tsx \
  src/components/Project/create-project/steps/Step3.test.tsx \
  src/components/Project/create-project/steps/Step5.test.tsx \
  src/components/Project/create-project/createProject.test.tsx \
  src/components/Project/view-project/ViewProjectHierarchyTree.test.tsx \
  --coverage=false
```

Frontend static checks:

```bash
cd front-end
npm run lint
npx tsc --noEmit
```

Local migration/seed checks:

```bash
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml up --build -d
docker compose -f back-end/local/compose.yml -f back-end/local/compose.local.yml exec -T db psql -U postgres -d assurance_db < back-end/local/seed-cross-linking-fixtures.sql
```

Manual browser validation remains required before demo sign-off.

## 7. Manual Demo Script

1. Start backend locally with the local profile and local DB.
2. Start frontend on `http://localhost:3000` with `VITE_PROXY_TARGET=http://localhost:8080`.
3. Log in as the local Project Developer Alpha user.
4. Existing Regular VPA negative check:
   - select existing `Regular Case VPA`
   - verify Beta PoAs/Real Cases are not offered
5. Cross-link Real Case VPA:
   - select `Real Case VPA (cross linkage)`
   - select a Beta PoA
   - verify Real Case selector is hidden
   - finish project creation
   - verify created project is Alpha-owned and has selected Beta PoA as master and parent
6. Cross-link Regular Case VPA:
   - select `Regular Case VPA (cross linkage)`
   - verify Real Case selector is empty before PoA selection
   - select a Beta PoA with no Real Cases and verify clear empty state
   - switch to a Beta PoA with Real Cases
   - select one Beta Real Case VPA under that PoA
   - verify methodology filtering follows the selected Real Case
   - finish project creation
   - verify created project is Alpha-owned, master is selected Beta PoA, parent is selected Beta Real Case
7. Post-create visibility:
   - Alpha sees the created projects
   - Beta does not gain visibility/action access to Alpha-created projects
   - ARM/Admin can edit through the expected project edit path
   - tree shows selected ancestry without unrelated Beta sibling projects
   - dashboard/export/tree surfaces show normal VPA labels rather than `(cross linkage)` labels
8. Capture console/network evidence with no unexpected 4xx/5xx responses.

## 8. Risks And Open Implementation Choices

- The internal classification IDs/codes are implementation-chosen and should be easy to change if Gold Standard provides exact values later.
- The phrase "ARM edit access" likely maps to existing ARM project edit permissions; implementation should verify whether ARM also needs create access for these classifications before widening create permissions.
- Candidate detail response must stay intentionally narrow because it is the controlled cross-account read path.
- Tree/report label mapping may require backend DTO/report normalization plus a frontend fallback where components currently render raw lookup descriptions.
- Large cross-account candidate lists may need search/pagination later; first version uses minimal projection, deterministic sorting, and local performance checks.
- If request/batch code cannot safely mirror mapping rows, prefer an explicit effective-classification bucket lookup in `BatchTypeService`.

## 9. Integrated Review Notes

The reviewed plan incorporates:

- tech-lead finding: add explicit `gs.batch_mapping` work and batch acceptance tests
- tech-lead finding: restore Java integration fixtures, not only local SQL seed
- tech-lead finding: make update mutation behavior explicit and tested
- tech-lead finding: expand dashboard/report/export/display normalization work
- solutions-architect finding: make tree visibility an implementation task with pruned authorized lineage
- solutions-architect finding: define exact candidate-detail request/response boundary
- solutions-architect finding: add a canonical classification semantics layer
- solutions-architect finding: add local demo readiness gates and first-version candidate-list guardrails
