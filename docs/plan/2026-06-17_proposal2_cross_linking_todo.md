# Proposal 2 Cross-Linking Todo

Source plan: `docs/plan/2026-06-16_proposal2_cross_linking_final_plan.md`

## Sequential Tasks

- [x] Task 1A: Reference Data, Batch Mapping, And Started Artifact Verification
  - Added the two cross-link project classifications as inactive until Task 2+ create-flow support lands.
  - Added batch mapping compatibility for cross-link Real Case and Regular VPA.
  - Kept Liquibase and test reference data aligned.
  - Localhost check: batch request type mapping resolves cross-link Real Case to Real-plus-Regular and cross-link Regular to two-or-more-Regular behavior.

- [x] Task 1B: Backend Integration And Demo Fixtures
  - Added deterministic Alpha/Beta PD integration fixtures.
  - Added idempotent local demo seed data.
  - Included PoAs across statuses and Real Case VPAs with distinct methodologies.
  - Localhost check: Alpha and Beta PD demo accounts are present and usable with the seeded cross-link data.

- [x] Task 2: Backend Classification Helpers, Semantics, And Display Mapping
  - Add helper methods and canonical classification semantics.
  - Treat cross-link classifications as normal Real Case/Regular VPA outside the create selector.
  - Cover dashboard, export, report, batch, finance, registry, SDG, and tree/list display behavior.
  - Localhost check: project tree and batch request type behavior normalize cross-link classifications to the regular Real/Regular semantics.
  - Handover note: dashboard/report/export should still get a data-backed QA pass under Task 8.

- [x] Task 3: Shared Eligibility And Account Policy
  - Centralize effective account resolution.
  - Centralize parent eligibility validation.
  - Reject PD account spoofing and invalid PoA/Real Case pairings.
  - Localhost check: native cross-account parent creation is rejected, PD account spoofing is rejected, and cross-link children persist under the creating Alpha PD account.

- [x] Task 4: Backend Candidate APIs
  - Update candidate PoA and Real Case VPA endpoints for cross-link rules.
  - Add `POST /projects/candidate-parent-details`.
  - Keep normal `GET /projects/{id}` authorization unchanged.
  - Localhost check: Alpha PD can list Beta candidate PoAs and candidate Real Case VPAs through the candidate APIs, while normal direct `GET /projects/{id}` remains unauthorized for those Beta parent projects.

- [x] Task 5: Backend Create/Update Validation
  - Validate cross-link hierarchy before persistence.
  - Persist ownership to the creating/effective PD account.
  - Define and test update behavior for account/classification/master/parent changes.
  - Localhost check: created Alpha-owned cross-link Real and Regular projects retain Beta lineage, and update attempts to change immutable account/master fields are rejected.

- [x] Task 6: Frontend Classification Helpers, Schema, And API Contracts
  - Add frontend classification helpers.
  - Add validation for cross-link Real and cross-link Regular.
  - Add typed candidate-detail API contracts.
  - Localhost check: the create wizard exposes cross-link classifications and uses candidate-detail hydration for cross-account parent data.

- [x] Task 7: Frontend Create Wizard Flow
  - Show PoA-only selection for cross-link Real.
  - Show PoA plus Real Case selection for cross-link Regular.
  - Reset stale selections and use candidate-detail hydration for cross-account parents.
  - Localhost check: cross-link Real shows the PoA selector only; cross-link Regular shows PoA and Real Case selectors, and selecting a Beta PoA hydrates the related Beta Real Case options.

- [x] Task 8: Authorized Tree/List, Dashboard, Export, And Reporting Surfaces
  - Ensure linked parent PDs do not gain access.
  - Render pruned authorized tree lineage for cross-linked projects.
  - Normalize labels to normal Real Case/Regular VPA outside create flow.
  - Completed slice: Alpha can see Alpha-owned cross-link children, Beta cannot list or direct-GET Alpha cross-link children, and authorized tree lineage is pruned correctly.
  - Evidence: `ProjectApiIntegrationTest` covers Alpha/Beta cross-link visibility and pruned tree lineage; `ProjectAccessPolicyTest` and `ProjectApiIntegrationTest#testUpdateProjectPermission` cover Admin/ARM edit permission shape.
  - Evidence: `CommonDashboardConverterTest`, `DashboardRequestSpecFactoryTest`, and `CommonDashboardReportRowPopulatorTest` cover dashboard filter expansion, dashboard DTO label normalization, and common-dashboard export label normalization for cross-link classifications.
  - Evidence: status reporting is grouped by project type/request type through `gs.v_status_report_projects` and `gs.v_status_report_requests`, so it has no cross-link project-classification label to leak; `StatusReportApiIntegrationTest` covers Admin/ARM access to the reporting endpoints.

- [x] Task 9: Demo Readiness And Runbook
  - Document local setup, migration, seed, demo, evidence, and cleanup steps.
  - Include local readiness gates and stop conditions.
  - Verify clean local database migration and seed readiness for the demo.
  - Added `docs/mops/features/proposal2-cross-linking-demo.md` with clean database reset, migration, idempotent seed, login, browser smoke-test, dashboard/export/report evidence capture, cleanup steps, and an evidence manifest.
  - Verified clean-db migration plus idempotent seed replay with direct SQL checks for cross-link classification rows, batch mappings, and projects `910101` through `910105`.

## Handover Gaps

- Manual dashboard table evidence gate: request rows now exist for the browser-created cross-link children, and the authenticated common-dashboard DTO shows normal Real Case VPA / Regular VPA labels. Still capture the visible dashboard table after clearing the default First RoR Start Date filter or advancing the requests into a round.
- Manual export/reporting evidence gate: common-dashboard export returned `202 Accepted`, but the asynchronous spreadsheet artifact was not captured. Status reporting has no project-classification label to normalize; capture either the expected project-type/request-type grouping after the requests become reportable or record that the `In Preparation` demo requests do not appear.
- Optional role-evidence gate: capture an ARM browser screenshot showing edit access if stakeholders want role-specific visual evidence. Backend permission coverage already exists.

## Completion Gates

- [x] Backend unit tests pass for changed service/helper logic.
  - Verified with `mvn -f back-end/pom.xml test -Dtest=CandidateParentEligibilityPolicyTest,CandidateParentProjectFinderTest,ProjectClassificationSemanticsTest,ProjectServiceTest -DfailIfNoTests=false`.
- [x] Backend integration tests pass for candidate APIs, create/update validation, tree authorization, and batch mapping.
  - Verified with `mvn -f back-end/pom.xml test -Dtest=ProjectApiIntegrationTest -DfailIfNoTests=false`.
- [x] Frontend focused tests pass for helper/schema/create wizard behavior.
  - Verified with `(cd front-end && $HOME/.nvm/versions/node/v20.19.0/bin/node ./node_modules/jest/bin/jest.js --runTestsByPath src/components/Project/create-project/steps/Step1.test.tsx src/components/Project/create-project/steps/Step3.test.tsx src/components/Project/create-project/createProject.test.tsx --coverage=false --maxWorkers=50%)`.
- [x] Local migration and seed checks pass.
  - Verified with clean local Compose volume, Liquibase update, idempotent seed replay, and direct SQL checks for cross-link classifications, batch mappings, and seeded Beta fixture projects.
- [x] Focused Task 8 backend checks pass.
  - Verified with `mvn -f back-end/pom.xml test -Dtest=ProjectApiIntegrationTest,ProjectClassificationSemanticsTest,CommonDashboardConverterTest,DashboardRequestSpecFactoryTest,CommonDashboardReportRowPopulatorTest,StatusReportApiIntegrationTest,ProjectAccessPolicyTest -DfailIfNoTests=false`.
- [x] Focused cross-link frontend checks pass.
  - Verified with `(cd front-end && $HOME/.nvm/versions/node/v20.19.0/bin/node ./node_modules/jest/bin/jest.js --runTestsByPath src/components/Project/ProjectClassificationSemantics.test.ts src/components/Project/create-project/steps/Step1.test.tsx src/components/Project/create-project/steps/Step3.test.tsx src/components/Project/create-project/createProject.test.tsx src/components/Project/view-project/ViewProjectHierarchyTree.test.tsx --coverage=false --maxWorkers=50%)`.
- [x] Manual browser demo passes on `http://localhost:3000`.
  - Completed: login, selector behavior, cross-linkage helper note, candidate hydration, full React wizard submit for UI-created `GS910108` and `GS910109`, request creation for both UI-created projects, visibility, tree authorization, normalized detail labels, linked parent PD access denial, dashboard DTO normalization, and batch type mapping.
  - Verified with screenshot walkthrough and evidence manifest under `docs/mops/evidence/proposal2-cross-linking-demo/2026-06-17/`.
- [x] Focused helper text regression check passes.
  - Verified with `(cd front-end && $HOME/.nvm/versions/node/v20.19.0/bin/node ./node_modules/jest/bin/jest.js --runTestsByPath src/components/Project/create-project/HelperText.test.tsx src/components/Project/create-project/steps/Step1.test.tsx --coverage=false --maxWorkers=50%)`.
  - Remaining for non-core visual handoff: visible dashboard table screenshot after clearing/avoiding the default First RoR filter, asynchronous export spreadsheet artifact, status-report evidence after requests become reportable, and optional ARM browser edit screenshot.
