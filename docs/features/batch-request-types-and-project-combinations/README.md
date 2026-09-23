---
name: batch-request-types-and-project-combinations
description: Documents batch request type definitions, POA/VPA combinations, and candidate-selection validation rules.
---

# Batch request types and POA/VPA combinations

## Source of truth and request flow

Batch types are reference data in `gs.batch_type`. The current definitions and
the request-type/project-classification mappings are created in
`data-migration/migrations/001410-add-batch-mappings.sql`. The API uses those
mappings rather than a hard-coded request-type switch:

`POST /batch-requests/types` -> `BatchRequestController` -> `BatchTypeService.getBatchTypes` -> `BatchMappingRepository` -> batch types mapped for the request type and the selected project's classification.

Candidate projects follow the same mapping:

`POST /batch-requests/candidate-projects` -> `BatchRequestController` -> `BatchTypeService.getBatchCandidateProjects` -> validate that the requested batch type is mapped -> `ProjectFinder.getAllChildProjects` -> classification/parent predicate.

Batch creation accepts `requestTypeId`, `batchTypeId`, and at least two
`projectRequestInfos` in `BatchRequestCreateDTO`. Creation currently validates
the minimum project count and creates requests; the type-specific candidate
endpoint is responsible for restricting the selectable project set.

## Defined batch types

| ID | Code | Description | Required project shape |
| ---: | --- | --- | --- |
| 0 | `POA_PLUS_ONE_REAL_CASE` | PoA + 1 Real Case | One PoA parent and one or more Real Case VPA children under it. The name says “1”; the child predicate allows all matching Real Case children returned for the PoA. |
| 1 | `POA_PLUS_ONE_OR_MORE_REAL_CASE` | PoA + 1 or more Real Case | One PoA parent and one or more Real Case VPA children under it. |
| 2 | `REAL_CASE_PLUS_ONE_OR_MORE_REGULAR_CASE` | Real Case + 1 or more Regular Case | One Real Case VPA parent and one or more Regular VPA children whose parent is that Real Case. |
| 3 | `TWO_OR_MORE_REGULAR_CASE` | 2 or more Regular Case | One Regular VPA parent and one or more different Regular VPA projects with the same parent. |

IDs 0–3 are inserted as the current known batch types by migration 001410.
`BatchType.isTwoOrMoreVpaCpa()` also recognizes legacy ID 4, which uses the
legacy VPA/CPA predicate: a legacy VPA/CPA parent plus different legacy VPA/CPA
children sharing that parent. ID 4 is not inserted by migration 001410 and is
therefore legacy/reference-data dependent.

## Valid POA/VPA combinations

The combinations below describe the project classifications accepted by the
implemented child predicates. “Under” means the relevant master/parent links
must match, not merely that the projects belong to the same account.

| Batch type | Parent selected first | Additional projects allowed |
| --- | --- | --- |
| 0 or 1 | PoA | Real Case VPAs whose `masterProject` is the selected PoA |
| 2 | Real Case VPA | Regular VPAs whose `parentProject` is the selected Real Case VPA |
| 3 | Regular VPA | Other Regular VPAs with the same parent as the selected Regular VPA; the selected project itself is excluded |
| 4 (legacy) | Legacy VPA/CPA | Other legacy VPA/CPAs with the same parent; the selected project itself is excluded |

The ordinary child predicate is implemented by
`DefaultIsDirectChildProjectPredicate`. It treats a PoA as a parent of Real
Case VPAs, a Real Case VPA as a parent of Regular VPAs, and a Regular VPA as a
parent of sibling Regular VPAs. The legacy predicate is selected only for
batch type 4 by `IsDirectChildProjectPredicateFactory`.

Cross-link classifications inherit the matching normal VPA batch behavior:

- `REAL_CASE_VPA_CROSS_LINKAGE` is mapped and batched like `REAL_CASE_VPA`.
- `REGULAR_VPA_CROSS_LINKAGE` is mapped and batched like `REGULAR_VPA`.

The parity is covered by `CrossLinkingReferenceDataIntegrationTest`; the
cross-link mappings must remain present if the classification is expected to
appear in batch type lookup.

## Selection and validation restrictions

### Batch type and request type

- The offered batch types are the rows matching both the request type and the
  selected project's exact classification ID in `gs.batch_mapping`.
- A candidate-project request whose batch type is not one of those rows is
  rejected by `BatchTypeService` with a `BatchRequestException`.
- A project must have at least two entries in `projectRequestInfos`; otherwise
  `BatchRequestCreateValidator` rejects the batch.

### Parent/child hierarchy

The candidate endpoint only returns projects from the same account as the
selected parent (`ProjectFinder.getAllChildProjects`), and then applies the
batch-specific predicate. Consequently, a project with the right VPA
classification but the wrong master/parent link is not selectable.

For direct parent selection outside batch candidate lookup,
`CandidateParentEligibilityPolicy` applies the same structural rules:

- Real Case VPA: the selected master and parent must be the same PoA.
- Regular VPA: the master must be a PoA, and the parent must be a Real Case VPA
  under that PoA.
- Candidate PoA selection must actually identify a PoA.
- Candidate Real Case selection for a Regular VPA must identify a Real Case VPA
  under the selected PoA.

### Account restrictions and cross-link exceptions

- Normal Real Case and Regular VPA parent selection is account-scoped.
- Admin/ARM users can use all-account PoA candidates for eligible normal VPA
  flows, as implemented by `CandidateParentProjectFinder`.
- Cross-link Real Case and cross-link Regular classifications can use
  cross-account parents. For cross-link Regular VPA, the Real Case candidates
  are still restricted to Real Case VPAs under the selected PoA.
- A linked parent's account does not become the account owner of the new
  project; the creating/project-developer account remains the owner.

### Dependent selector behavior

For cross-link Regular VPA, Real Case candidates are not available until a PoA
is selected, and changing the PoA must invalidate any stale Real Case choice.
For cross-link Real Case VPA, only a PoA is required; a Real Case selector is
not part of the combination. These UI rules correspond to the candidate API
requests and the server-side hierarchy checks, so the API remains the final
authority.

## Handled failures

- Unknown project IDs produce a not-found error through `ProjectFinder`.
- An unmapped request-type/classification/batch-type combination is rejected.
- Wrong project classifications or mismatched hierarchy links are rejected by
  `CandidateParentEligibilityPolicy` when parent details are validated.
- A batch with fewer than two projects is rejected before persistence.

## Unhandled or notable gaps

- `BatchRequestCreateValidator` does not itself re-check that every submitted
  project matches the selected batch type; it only checks the count. Callers
  should use the candidate-project endpoint, and any future hardening should
  validate the complete submitted set server-side before persistence.
- Batch type 0 and batch type 1 currently use the same default child predicate,
  so their observable candidate shape is equivalent even though their names
  distinguish “1” from “1 or more”.
- Legacy batch type 4 is recognized in Java but is not defined by the current
  001410 seed migration.

## Implementation references

- `data-migration/migrations/001410-add-batch-mappings.sql`
- `back-end/src/main/java/org/goldstandard/assurance/api/entity/lookup/BatchType.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/BatchTypeService.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/entity/IsDirectChildProjectPredicateFactory.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/entity/DefaultIsDirectChildProjectPredicate.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/CandidateParentProjectFinder.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/CandidateParentEligibilityPolicy.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/BatchRequestCreateValidator.java`
- `back-end/src/test/java/org/goldstandard/assurance/api/BatchRequestApiIntegrationTest.java`
- `back-end/src/test/java/org/goldstandard/assurance/api/CrossLinkingReferenceDataIntegrationTest.java`
