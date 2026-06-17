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

- [ ] Task 8: Authorized Tree/List, Dashboard, Export, And Reporting Surfaces
  - Ensure linked parent PDs do not gain access.
  - Render pruned authorized tree lineage for cross-linked projects.
  - Normalize labels to normal Real Case/Regular VPA outside create flow.
  - Completed slice: Alpha can see Alpha-owned cross-link children, Beta cannot list or direct-GET Alpha cross-link children, and authorized tree lineage is pruned correctly.

- [ ] Task 9: Demo Readiness And Runbook
  - Document local setup, migration, seed, demo, evidence, and cleanup steps.
  - Include local readiness gates and stop conditions.
  - Verify demo can run from a clean local database after migrations.

## Handover Gaps

- Task 8: Seed or identify request/report data that exercises cross-link Real Case and Regular projects, then verify normalized labels and access boundaries in dashboard, export, and reporting screens.
- Task 8: Verify ARM-role edit behavior with an ARM credential or seeded ARM test user if stakeholders require that exact role path; admin edit was verified locally with `patslaurel`.
- Task 9: Convert the localhost evidence into a repeatable clean-db runbook covering setup, migration, seed, login, smoke-test, evidence capture, and cleanup.
- Manual demo gate: Capture a full React wizard submit pass; selector behavior and create APIs were verified already.

## Completion Gates

- [x] Backend unit tests pass for changed service/helper logic.
  - Verified with `mvn -f back-end/pom.xml test -Dtest=CandidateParentEligibilityPolicyTest,CandidateParentProjectFinderTest,ProjectClassificationSemanticsTest,ProjectServiceTest -DfailIfNoTests=false`.
- [x] Backend integration tests pass for candidate APIs, create/update validation, tree authorization, and batch mapping.
  - Verified with `mvn -f back-end/pom.xml test -Dtest=ProjectApiIntegrationTest -DfailIfNoTests=false`.
- [x] Frontend focused tests pass for helper/schema/create wizard behavior.
  - Verified with `node ./node_modules/jest/bin/jest.js --runTestsByPath src/components/Project/create-project/steps/Step1.test.tsx src/components/Project/create-project/steps/Step3.test.tsx src/components/Project/create-project/createProject.test.tsx --coverage=false --maxWorkers=50%`.
- [x] Local migration and seed checks pass.
- [ ] Manual browser demo passes on `http://localhost:3000`.
  - Completed: login, selector behavior, candidate hydration, API create/update guardrails, visibility, tree authorization, and batch type mapping.
  - Remaining: dashboard/report/export evidence and full React wizard submit capture.
