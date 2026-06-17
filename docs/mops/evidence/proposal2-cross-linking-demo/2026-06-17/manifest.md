# Proposal 2 Cross-Linking Demo Evidence Manifest

## Run Metadata

- Date: 2026-06-17
- Operator: Codex
- Branch: `feat/proposal-2-cross-linking`
- Runbook: `docs/mops/features/proposal2-cross-linking-demo.md`
- Walkthrough: `docs/mops/evidence/proposal2-cross-linking-demo/2026-06-17/demo-walkthrough.md`
- Local app: `http://localhost:3000`
- Backend: `http://localhost:8080`
- Browser evidence tool: Computer Use observed Chrome; Playwright/Chrome saved screenshots after `agent-browser` was unavailable on PATH.
- Screenshot hygiene: final Step 9+ evidence was captured after accepting the cookie banner so the evidence is unobstructed.

## Source Requirement Summary

Sources reviewed:

- PDF: `/Users/patslaurel/Downloads/AM-Cross linking regular case VPAs with different PDs-090626-025419.pdf`
- Gmail thread: `Thinking Machines Test Feature: Cross-Linking VPAs for Assurance`, thread id `19ecab5bfc8625e5`
- Plan: `docs/plan/2026-06-16_proposal2_cross_linking_final_plan.md`
- Todo: `docs/plan/2026-06-17_proposal2_cross_linking_todo.md`

Confirmed requirements:

- Proposal 2 adds two new project classifications rather than modifying the existing Regular VPA flow only.
- PD users can select `Real Case VPA (cross linkage)` and `Regular Case VPA (cross linkage)` in Create Project.
- The UI explains that cross linkage means the VPA's PD account differs from the PoA's PD account, or the Regular Case VPA's PD account differs from its Real Case VPA's PD account.
- Cross-link Real Case VPA selects a target PoA across PD accounts.
- Cross-link Regular Case VPA selects a target PoA plus a Real Case VPA under that PoA.
- PoA dropdown includes all PoAs across statuses, including New, Listed, and Design Certified.
- Real Case dropdown includes all Real Case VPAs under the selected PoA.
- New categories inherit existing request and batching behavior from matching Real/Regular VPA behavior.
- Analytics/dashboard/report/export/tree labels should roll up as normal `Real Case VPA` / `Regular VPA`, not introduce confusing new client-facing terms outside create-time selection.
- Created project stays owned by the creating PD; linked parent PD does not gain visibility or action access.
- ARM accounts should keep edit access.

## Completed Local Checks

| Check | Status | Evidence |
|---|---|---|
| Cross-linkage helper note added and tested | Passed | `HelperText.test.tsx`; `03b-cross-linkage-helper-note.png`. |
| Focused helper/Step 1 frontend tests | Passed | `(cd front-end && $HOME/.nvm/versions/node/v20.19.0/bin/node ./node_modules/jest/bin/jest.js --runTestsByPath src/components/Project/create-project/HelperText.test.tsx src/components/Project/create-project/steps/Step1.test.tsx --coverage=false --maxWorkers=50%)`. |
| React wizard submit for Regular Case VPA cross linkage | Passed | `13-regular-ui-review-before-finish.png`, `14-regular-ui-submit-success-tree.png`; created `GS910108`. |
| React wizard submit for Real Case VPA cross linkage | Passed | `13-real-ui-review-before-finish.png`, `14-real-ui-submit-success-tree.png`; created `GS910109`. |
| SQL lineage check for UI-created projects | Passed | `GS910108` and `GS910109` are owned by Alpha account `910001` and linked to Beta PoA/Real records. |
| Linked parent PD direct access denied | Passed | `17-beta-ui-submitted-regular-access-denied.png`. |
| React wizard request creation for UI-created projects | Passed | `GS910108` has request `1`; `GS910109` has request `2`; screenshots `20-regular-ui-request-created-detail.png`, `21-real-ui-request-created-detail.png`. |
| Common dashboard DTO normalization for request rows | Passed | `19-common-dashboard-api-normalized-rows.png` shows `GS910108` as `Regular VPA` and `GS910109` as `Real Case VPA`. |
| Common dashboard export endpoint | Accepted | Authenticated export endpoint returned `202 Accepted`; generated async artifact was not captured in this pass. |

Prior automated checks reported in the task todo:

| Check | Status | Evidence |
|---|---|---|
| Backend unit tests for changed service/helper logic | Passed | `mvn -f back-end/pom.xml test -Dtest=CandidateParentEligibilityPolicyTest,CandidateParentProjectFinderTest,ProjectClassificationSemanticsTest,ProjectServiceTest -DfailIfNoTests=false`. |
| Backend integration tests for candidate APIs, create/update validation, tree authorization, and batch mapping | Passed | `mvn -f back-end/pom.xml test -Dtest=ProjectApiIntegrationTest -DfailIfNoTests=false`. |
| Focused Task 8 backend checks | Passed | `mvn -f back-end/pom.xml test -Dtest=ProjectApiIntegrationTest,ProjectClassificationSemanticsTest,CommonDashboardConverterTest,DashboardRequestSpecFactoryTest,CommonDashboardReportRowPopulatorTest,StatusReportApiIntegrationTest,ProjectAccessPolicyTest -DfailIfNoTests=false`. |
| Focused cross-link frontend checks | Passed | `(cd front-end && $HOME/.nvm/versions/node/v20.19.0/bin/node ./node_modules/jest/bin/jest.js --runTestsByPath src/components/Project/ProjectClassificationSemantics.test.ts src/components/Project/create-project/steps/Step1.test.tsx src/components/Project/create-project/steps/Step3.test.tsx src/components/Project/create-project/createProject.test.tsx src/components/Project/view-project/ViewProjectHierarchyTree.test.tsx --coverage=false --maxWorkers=50%)`. |
| Local migration and seed checks | Passed | Clean local Compose volume, Liquibase update, idempotent seed replay, and direct SQL checks for cross-link classifications, batch mappings, and seeded Beta fixture projects. |

## UI-Created Demo Projects

| Project | GSID | Persisted classification | Owner account | Master project | Parent project | Evidence |
|---|---:|---|---|---:|---:|---|
| Cross-link Regular Case VPA | `GS910108` | `Regular Case VPA (cross linkage)` | Cross Link Alpha PD (`910001`) | `910102` | `910104` | `14-regular-ui-submit-success-tree.png`, `15-regular-ui-submitted-detail.png` |
| Cross-link Real Case VPA | `GS910109` | `Real Case VPA (cross linkage)` | Cross Link Alpha PD (`910001`) | `910102` | `910102` | `14-real-ui-submit-success-tree.png`, `16-real-ui-submitted-detail.png` |

SQL confirmation:

```text
id     gsid      classification                         account_id  owner                master_project_id  parent_project_id
910108 GS910108  Regular Case VPA (cross linkage)       910001      Cross Link Alpha PD  910102             910104
910109 GS910109  Real Case VPA (cross linkage)          910001      Cross Link Alpha PD  910102             910102
```

## UI-Created Demo Requests

| Project GSID | Request ID | Request name | Request status | Evidence |
|---|---:|---|---|---|
| `GS910108` | `1` | `Inclusion | Regular Case VPA no. 1` | `In Preparation` | `20-regular-ui-request-created-detail.png`, `19-common-dashboard-api-normalized-rows.png` |
| `GS910109` | `2` | `Design Certification | Real Case VPA no. 1` | `In Preparation` | `21-real-ui-request-created-detail.png`, `19-common-dashboard-api-normalized-rows.png` |

Common dashboard SQL confirmation:

```text
request_id  request_name                                request_type_id  gsid      project_classification_id  account_name
1           Inclusion | Regular Case VPA no. 1          1004             GS910108  11                         Cross Link Alpha PD
2           Design Certification | Real Case VPA no. 1  1003             GS910109  10                         Cross Link Alpha PD
```

## Manual Evidence Status

| Gate | Status | Notes |
|---|---|---|
| PD Alpha login and create flow entry | Passed | `01-alpha-login-home.png`, `02-create-project-step1-initial.png`. |
| Create options visible to PD users | Passed | `03-classification-options.png` shows both cross-link classifications. |
| Cross-linkage helper note visible | Passed | `03b-cross-linkage-helper-note.png`. |
| Cross-link Real Case VPA PoA-only selector | Passed | `04-real-cross-link-poa-only.png`. |
| All-status cross-account PoA list | Passed | `05-real-cross-link-poa-options.png` shows Beta PoAs in Design Certified, Listed, and New states. |
| Cross-link Regular Case VPA PoA plus Real Case selectors | Passed | `06-regular-cross-link-selectors-empty.png`. |
| Real Case VPAs filtered under selected PoA | Passed | `07-regular-cross-link-real-case-options.png` shows `Cross Link Beta Real Case - ACM0001 (GS910104)` after selecting `GS910102`. |
| Full Regular cross-link wizard submit | Passed | `13-regular-ui-review-before-finish.png`, `14-regular-ui-submit-success-tree.png`; created `GS910108`. |
| Full Real cross-link wizard submit | Passed | `13-real-ui-review-before-finish.png`, `14-real-ui-submit-success-tree.png`; created `GS910109`. |
| Created cross-link children owned by creating PD | Passed | SQL evidence above; details visible as Alpha in `15-regular-ui-submitted-detail.png` and `16-real-ui-submitted-detail.png`. |
| Normalized project-detail labels | Passed | Details show `Regular VPA` and `Real Case VPA`, not cross-link labels, in `15-regular-ui-submitted-detail.png` and `16-real-ui-submitted-detail.png`. |
| Linked parent PD direct access denied | Passed | `17-beta-ui-submitted-regular-access-denied.png` shows Beta PD direct-opening `GS910108` reaches `NoAccess`. |
| Request rows | Passed | Requests `1` and `2` were created through the React wizard for `GS910108` and `GS910109`. |
| Dashboard DTO rows | Passed | `19-common-dashboard-api-normalized-rows.png` shows normalized dashboard DTO labels. |
| Dashboard browser table | Partially captured | `18-common-dashboard-default-filter-no-results.png` shows the page's default First RoR Start Date filter excludes newly created `In Preparation` rows with no first-round start date. |
| Common dashboard export | Accepted, artifact not captured | Authenticated export endpoint returned `202 Accepted`; export generation is asynchronous. Backend export label normalization is covered by focused tests listed above. |
| Status report | Not captured | Created requests are still `In Preparation` and have no first-round start/status-report grouping yet; focused backend tests cover endpoint access and grouping behavior. |
| ARM browser edit access | Not captured | Backend tests cover ARM edit permission shape; browser screenshot can be captured if stakeholder video needs it. |

## Satisfaction Matrix

| Requirement | Status | Evidence |
|---|---|---|
| Proposal 2 uses new classifications | Satisfied | `03-classification-options.png`; DB persisted classifications for `GS910108` and `GS910109`. |
| PD users can create/select cross-link categories | Satisfied | UI-created `GS910108` and `GS910109`. |
| Cross-linkage explanatory note is visible | Satisfied | `03b-cross-linkage-helper-note.png`; `HelperText.test.tsx`. |
| Cross-link Real selects target PoA only | Satisfied | `04-real-cross-link-poa-only.png`; UI-created `GS910109`. |
| Cross-link Regular selects PoA plus Real Case under selected PoA | Satisfied | `06-regular-cross-link-selectors-empty.png`, `07-regular-cross-link-real-case-options.png`; UI-created `GS910108`. |
| PoA dropdown includes all statuses across accounts | Satisfied | `05-real-cross-link-poa-options.png`. |
| Request/batch behavior inherited | Satisfied | Request rows were created through the React wizard; focused backend tests cover batch mapping behavior. |
| Dashboard/report/export/tree labels avoid confusing cross-link terms | Mostly satisfied | Project detail screenshots and authenticated dashboard DTO evidence show normalized labels; visible dashboard table is still hidden by the default First RoR filter until cleared/advanced, and async export artifact is not captured. |
| Created project remains owned by creating PD | Satisfied | SQL evidence for `account_id = 910001` on `GS910108` and `GS910109`; submit-success tree screenshots show Alpha visibility. |
| Linked parent PD has no visibility/action access | Satisfied | `17-beta-ui-submitted-regular-access-denied.png`; project parent IDs point to Beta projects but Beta cannot open Alpha child. |
| ARM edit access | Satisfied by automated/backend evidence | Todo lists `ProjectAccessPolicyTest` and update permission coverage; browser ARM screenshot not captured in this pass. |

## Artifact Index

- `01-alpha-login-home.png`: Alpha PD authenticated home/my-requests page.
- `02-create-project-step1-initial.png`: Create Project step 1 before classification selection.
- `03-classification-options.png`: PD-visible classification options, including both cross-link labels.
- `03b-cross-linkage-helper-note.png`: Project Classification helper note explaining cross linkage.
- `04-real-cross-link-poa-only.png`: Real Case VPA cross-linking shows PoA selector only.
- `05-real-cross-link-poa-options.png`: Cross-account Beta PoA options across statuses.
- `06-regular-cross-link-selectors-empty.png`: Regular Case VPA cross-linking shows PoA and Real Case selectors.
- `07-regular-cross-link-real-case-options.png`: Real Case options filtered under selected Beta PoA.
- `08-alpha-project-tree-cross-link-children.png`: Earlier Alpha PD project-tree view after API-created demo children.
- `08-invalid-alpha-global-projects-route-unauthorized.png`: Invalid earlier capture of the global `/projects` route. Project Developer users are not authorized for that route, so this image should not be used as pass evidence.
- `09-alpha-regular-cross-link-detail.png`: Earlier Alpha detail view for API-created `GS910107`, normalized `Regular VPA` label.
- `10-alpha-real-cross-link-detail.png`: Earlier Alpha detail view for API-created `GS910106`, normalized `Real Case VPA` label.
- `11-beta-direct-access-denied.png`: Earlier Beta denial for API-created `GS910107`.
- `12-regular-ui-step1-ready-to-submit.png`: Regular cross-link wizard Step 1 filled before continuing.
- `12-real-ui-step1-ready-to-submit.png`: Real cross-link wizard Step 1 filled before continuing.
- `13-regular-ui-review-before-finish.png`: Regular cross-link Review step before clicking `Finish`.
- `13-real-ui-review-before-finish.png`: Real cross-link Review step before clicking `Finish`.
- `14-regular-ui-submit-success-tree.png`: Project tree after clicking `Finish` for Regular cross-link; shows `GS910108`.
- `14-real-ui-submit-success-tree.png`: Project tree after clicking `Finish` for Real cross-link; shows `GS910109`.
- `15-regular-ui-submitted-detail.png`: Alpha detail view for UI-created `GS910108`, normalized `Regular VPA` label.
- `16-real-ui-submitted-detail.png`: Alpha detail view for UI-created `GS910109`, normalized `Real Case VPA` label.
- `17-beta-ui-submitted-regular-access-denied.png`: Beta PD denied direct access to UI-created `GS910108`.
- `18-common-dashboard-default-filter-no-results.png`: Admin common dashboard page with default First RoR Start Date filter excluding newly created request rows.
- `19-common-dashboard-api-normalized-rows.png`: Authenticated common-dashboard API response showing request rows and normalized `Regular VPA` / `Real Case VPA` classifications.
- `20-regular-ui-request-created-detail.png`: Alpha detail view for `GS910108` after creating request `1`.
- `21-real-ui-request-created-detail.png`: Alpha detail view for `GS910109` after creating request `2`.
