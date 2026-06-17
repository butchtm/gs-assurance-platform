# Proposal 2 Cross-Linking Todo

Source plan: `docs/plan/2026-06-16_proposal2_cross_linking_final_plan.md`

## Sequential Tasks

- [x] Task 1A: Reference Data, Batch Mapping, And Started Artifact Verification
  - Added the two cross-link project classifications as inactive until Task 2+ create-flow support lands.
  - Added batch mapping compatibility for cross-link Real Case and Regular VPA.
  - Kept Liquibase and test reference data aligned.
  - Review follow-up: R2/R3 fixes applied; pending review.

- [x] Task 1B: Backend Integration And Demo Fixtures
  - Added deterministic Alpha/Beta PD integration fixtures.
  - Added idempotent local demo seed data.
  - Included PoAs across statuses and Real Case VPAs with distinct methodologies.
  - Review follow-up: R2/R3 fixes applied; pending review.

- [ ] Task 2: Backend Classification Helpers, Semantics, And Display Mapping
  - Add helper methods and canonical classification semantics.
  - Treat cross-link classifications as normal Real Case/Regular VPA outside the create selector.
  - Cover dashboard, export, report, batch, finance, registry, SDG, and tree/list display behavior.

- [ ] Task 3: Shared Eligibility And Account Policy
  - Centralize effective account resolution.
  - Centralize parent eligibility validation.
  - Reject PD account spoofing and invalid PoA/Real Case pairings.

- [ ] Task 4: Backend Candidate APIs
  - Update candidate PoA and Real Case VPA endpoints for cross-link rules.
  - Add `POST /projects/candidate-parent-details`.
  - Keep normal `GET /projects/{id}` authorization unchanged.

- [ ] Task 5: Backend Create/Update Validation
  - Validate cross-link hierarchy before persistence.
  - Persist ownership to the creating/effective PD account.
  - Define and test update behavior for account/classification/master/parent changes.

- [ ] Task 6: Frontend Classification Helpers, Schema, And API Contracts
  - Add frontend classification helpers.
  - Add validation for cross-link Real and cross-link Regular.
  - Add typed candidate-detail API contracts.

- [ ] Task 7: Frontend Create Wizard Flow
  - Show PoA-only selection for cross-link Real.
  - Show PoA plus Real Case selection for cross-link Regular.
  - Reset stale selections and use candidate-detail hydration for cross-account parents.

- [ ] Task 8: Authorized Tree/List, Dashboard, Export, And Reporting Surfaces
  - Ensure linked parent PDs do not gain access.
  - Render pruned authorized tree lineage for cross-linked projects.
  - Normalize labels to normal Real Case/Regular VPA outside create flow.

- [ ] Task 9: Demo Readiness And Runbook
  - Document local setup, migration, seed, demo, evidence, and cleanup steps.
  - Include local readiness gates and stop conditions.
  - Verify demo can run from a clean local database after migrations.

## Completion Gates

- [ ] Backend unit tests pass for changed service/helper logic.
- [ ] Backend integration tests pass for candidate APIs, create/update validation, tree authorization, and batch mapping.
- [ ] Frontend focused tests pass for helper/schema/create wizard behavior.
- [ ] Local migration and seed checks pass.
- [ ] Manual browser demo passes on `http://localhost:3000`.
