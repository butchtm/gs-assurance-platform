# Send SDG project POSTs with a fixed content length

## Scope

Repository/revision: `gs-assurance-platform`, current working tree.

Agreed requirements:

- Keep posting the existing SDG project JSON to `/api/projects/GS<gsid>/`.
- Send a fixed-length request body so the request does not use chunked transfer encoding.
- Preserve the JSON payload, authorization header, and content type.

Acceptance criteria:

- AC1: The configured SDG REST client buffers each request and sends a `Content-Length` header derived from the serialized body.
- AC2: The existing JSON request body and route remain unchanged.
- AC3: A regression test asserts the expected JSON body, a `Content-Length` header, and no `Transfer-Encoding: chunked` header.

Assumptions and decisions:

- Spring Framework 6.2.9 is in use through Spring Boot 3.5.4. Its `BufferingClientHttpRequestFactory` calculates the content length from the buffered bytes before delegating, so the existing Jackson serialization is preserved.
- Buffering is acceptable for this small project payload and response.

Non-goals:

- Changing the SDG API payload schema, endpoint, authentication, asynchronous update flow, or receiver implementation.
- Adding dependencies or manually calculating `Content-Length`.

## Tasks

### T1: Configure fixed-length SDG HTTP requests

- Implementation capability / responsible workstream: Java implementation.
- Owned existing files: `back-end/src/main/java/org/goldstandard/assurance/api/configuration/SdgApiClientConfiguration.java`.
- Proposed new files: none.
- Expected behavior and affected contracts: Construct the SDG `RestTemplate` with a `BufferingClientHttpRequestFactory` wrapping the existing `SimpleClientHttpRequestFactory`. Spring will set `Content-Length` from the buffered body and retain the existing JSON message conversion.
- Prerequisite tasks/files and external inputs: none.
- Acceptance criteria covered: AC1, AC2.
- Validation criteria, commands where known, and testing capability: T2 verifies headers and body with WireMock; run focused Maven test and Spotless.
- Dependency changes and license evidence/unknowns: none.
- Compatibility, data migration, rollout, and recovery concerns: Request/response streams are buffered in memory per call; project payloads and expected API responses are small. No migration required.

### T2: Assert SDG request framing

- Implementation capability / responsible workstream: Java test engineering.
- Owned existing files: `back-end/src/test/java/org/goldstandard/assurance/api/service/external/sdg/RestSdgApiClientTest.java`.
- Proposed new files: none.
- Expected behavior and affected contracts: Keep the current JSON fixture match and assert `Content-Length` is present while `Transfer-Encoding` is absent.
- Prerequisite tasks/files and external inputs: T1.
- Acceptance criteria covered: AC2, AC3.
- Validation criteria, commands where known, and testing capability: From `back-end`, run `JAVA_HOME=/home/butchtm/.sdkman/candidates/java/21.0.8-librca mvn -Dtest=RestSdgApiClientTest test`; run the repository Spotless check.
- Dependency changes and license evidence/unknowns: none.
- Compatibility, data migration, rollout, and recovery concerns: WireMock verifies wire headers and decoded body but does not exercise the local SDG listener. Confirm against that listener after deploying the change.

## Delivery and validation

Execution order: T1, then T2, then focused validation, code review, and documentation review.

Testing, review, documentation responsibilities:

- Test engineering: add framing assertions and run the focused test.
- Review: inspect correctness, payload preservation, and memory implications.
- Documentation: no user-facing documentation change is needed; this changes transport framing while preserving the documented endpoint and payload contract.

Unverified assumptions / blockers: The local SDG listener's chunked-body handling is not exercised by WireMock. The fixed-length request should address the observed `Transfer-Encoding: chunked` issue, but a live local call remains the end-to-end confirmation.

Checks already observed: Spring Framework 6.2.9 source confirms `BufferingClientHttpRequestFactory` buffers the request and `AbstractBufferingClientHttpRequest` sets `Content-Length` to the buffered byte count when it is not already set.
