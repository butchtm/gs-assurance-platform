# Development tasks: Verify SDG project POST payload

## Scope

Repository/revision: `gs-assurance-platform`, current working tree.

Agreed requirements: Exercise the SDG REST client's POST to `/api/projects/<GSID>/` and verify the request body matches the supplied VPA project example, including `poa_project_id` and nested crediting-period methodology data.

Acceptance criteria:

- AC1: The test fails if the SDG client does not send one POST to the GSID-specific endpoint.
- AC2: The test verifies the JSON request body against the supplied sample structure.
- AC3: The test verifies the JSON content type and `Authorization: Api-Key ...` header.

Assumptions and decisions: Use the existing WireMock test and expected-body fixture. `GS23982` is the project's GSID; `23980` is the POA parent ID.

Non-goals: No production code, endpoint, payload, credentials, or dependency changes; no call to the live SDG API.

## Tasks

### T1: Assert the VPA SDG request contract

- Implementation capability / responsible workstream: Test authoring (`software-testing`).
- Owned existing files: `back-end/src/test/java/org/goldstandard/assurance/api/service/external/sdg/RestSdgApiClientTest.java`; `back-end/src/test/resources/json/expected-sdg-project-request-body.json`.
- Proposed new files: None.
- Expected behavior and affected contracts: The test sends a sample `SdgProject` through `RestSdgApiClient` to WireMock and verifies exactly one POST at `/api/projects/GS23982/` with the supplied headers and JSON structure.
- Prerequisite tasks/files and external inputs: Existing WireMock extension and `SdgProject`/crediting-period DTOs; no live service or API key.
- Acceptance criteria covered: AC1, AC2, AC3.
- Validation criteria, commands where known, and testing capability: Run `JAVA_HOME=/home/butchtm/.sdkman/candidates/java/21.0.8-librca mvn -Dtest=RestSdgApiClientTest test` from `back-end`; Spotless must pass.
- Dependency changes and license evidence/unknowns: None.
- Compatibility, data migration, rollout, and recovery concerns: Test-only change; no runtime impact or data migration.

## Delivery and validation

Execution order: T1, then focused test and formatting validation.

PR shape: One small conventional change.

Testing, review, documentation responsibilities: Test authoring and focused execution apply. Code review applies to the test diff. Separate product documentation update is not applicable because the HTTP contract and runtime behavior do not change.

Unverified assumptions / blockers: None identified.

Checks already observed: Existing `RestSdgApiClientTest` uses WireMock and matches URL, headers, and a JSON fixture; no new test has been run for this request yet.
