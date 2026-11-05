---
name: local-ap-sdg-sync
description: Configure a local Assurance Platform backend to send saved project data to a local SDG Tool API.
---

# Configure local AP to sync projects to the SDG Tool

## Objective

Configure a local Assurance Platform (AP) backend to send updated project data to a local SDG Tool API at `http://localhost:8000`, including crediting periods and their methodologies.

The current AP REST client posts to:

```text
<sdg.tool.project.api.host>/api/projects/<GSID>/
```

For this setup, that becomes `http://localhost:8000/api/projects/GS12345/`. AP sends `POST` with a JSON body and an `Authorization: Api-Key <key>` header. The SDG Tool must accept this GSID-specific route; an endpoint that accepts only `POST /api/projects` is not the route AP currently calls.

## Client selection

`SdgApiClientConfiguration` selects the SDG client based on the active Spring profiles and `sdg.tool.project.api.enabled`:

- With `local` active and `test` inactive, set `sdg.tool.project.api.enabled=true` to select `RestSdgApiClient`. When the property is missing or false, AP uses `StubSdgApiClient`.
- With `test` active, AP always uses `StubSdgApiClient`, including when `local` is also active and the property is true.
- Without `local` or `test`, AP keeps its production behavior: it uses `RestSdgApiClient` when a host is configured and a no-op client otherwise.

The local SDG instance used for this setup accepts the GSID-specific route shown above, so no endpoint-path change is needed.

## Data flow when a project is updated

1. The project editor sends `PUT /projects/{projectId}` to AP's backend with the project update payload.
2. `ProjectService.updateProject` applies the allowed fields. For admin or ARM users, it rebuilds crediting periods from the submitted update. For project developers, it keeps the existing crediting periods.
3. `ProjectTransaction.performUpdate` saves the project and crediting periods in the database transaction. The service then schedules external updates asynchronously for the saved project and any updated child projects.
4. The external updater converts the saved AP project and account to an SDG payload. The GSID is used to build the request URL and is excluded from the JSON body. The payload includes project information and `crediting_periods`; each period includes its ID, `start_date`, `end_date`, and `methodologies` (`id`, `description`, and `version`).
5. `RestSdgApiClient` posts that JSON to `<host>/api/projects/<GSID>/` with `Content-Type: application/json` and `Authorization: Api-Key <token>`. AP buffers the serialized JSON and sends its byte length in `Content-Length`, rather than using chunked transfer encoding.
6. The client logs the HTTP response. If the outbound call fails with a REST client error, AP records the failed SDG update for its scheduled retry processing.

The project update response is returned after the project has been saved and the outbound work has been queued; it does not wait for the SDG API response. Check backend logs and the SDG Tool logs to confirm the asynchronous call completed.

## Prerequisites

- AP backend and database are running locally.
- SDG Tool API is reachable from the AP backend process at `http://localhost:8000`.
- The SDG Tool accepts `POST /api/projects/<GSID>/` and authenticates `Authorization: Api-Key <key>`.
- You have an SDG Tool API key. Keep it out of source control and logs.
- If using `local-auth`, the local database contains the active user mapped by your local-auth alias.

## Procedure

### 1. Configure the local SDG connection

From the backend terminal, set the host, enable the local REST client, and provide your API key through environment variables:

```bash
export SDG_TOOL_PROJECT_API_ENABLED=true
export SDG_TOOL_PROJECT_API_HOST=http://localhost:8000
export SDG_TOOL_PROJECT_API_TOKEN='<your SDG Tool API key>'
```

Spring Boot maps these environment variables to `sdg.tool.project.api.enabled`, `.host`, and `.token`. These work with either the `local` profile alone or `local,local-auth`. Use the host only; do not append `/api/projects` because AP builds that path itself. Replace the placeholder with your key. Do not commit the value or put it in a tracked configuration file.

Alternatively, put these values in an ignored local configuration file such as `back-end/config/application-local-auth.yml` when using both `local` and `local-auth` profiles:

```yaml
sdg:
  tool:
    project:
      api:
        enabled: true
        host: http://localhost:8000
        token: ${SDG_TOOL_PROJECT_API_TOKEN}
```

Keep the key itself in the environment. The repository's `.gitignore` excludes `back-end/config/application-local-auth.yml`.

### 2. Start AP with local authentication

Configure your local-auth alias as described in [the local development runbook](../../local-development-runbook.md#opt-in-fake-local-credentials). Then run the backend from `back-end` with both profiles active. The backend also requires a private `LOCAL_AUTH_JWT_SECRET` of at least 32 characters.

```bash
export LOCAL_AUTH_JWT_SECRET='<at least 32 private local characters>'
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local,local-auth"
```

You may omit `local-auth` if your local AP setup uses its other supported authentication path; keep the `local` profile active to load local database settings.

### 3. Verify that the REST client is selected

At startup, confirm the backend log contains a message similar to:

```text
sdg api client configured with host http://localhost:8000
```

If it instead logs `stub sdg api client configured`, confirm `SDG_TOOL_PROJECT_API_ENABLED=true` is set, `local` is active, and `test` is not active. If it logs that no host is configured, verify the host environment variable name and value.

### 4. Update a project and confirm synchronization

Use the AP UI to update a project as an admin or ARM user, including a crediting period date or methodology change. Submit the form. The AP database update occurs before the asynchronous external call is scheduled.

Confirm all of the following:

- AP reports the project update succeeded.
- Backend logs show the SDG update attempt and HTTP response status.
- SDG Tool logs show a `POST` to `/api/projects/<GSID>/` with the expected project and crediting-period data.
- The request headers show `Content-Length`; `Transfer-Encoding: chunked` is absent.
- The SDG Tool project record reflects the update.

Project developers cannot change crediting periods through this update path; the backend retains their existing periods before syncing.

## Payload shape

AP sends the saved project's SDG representation, not the original UI update DTO. The payload includes fields such as `name`, `description`, `status`, `country_codes`, `annual_estimated_credits`, `carbon_stream`, `types`, `project_developer`, `programme_of_activities`, and `crediting_periods`. Internal AP `id` and `gsid` values are excluded from the JSON body; the GSID is used in the URL.

Example fragment:

```json
{
  "name": "Example project",
  "crediting_periods": [
    {
      "id": 1,
      "start_date": "2024-01-01",
      "end_date": "2028-12-31",
      "methodologies": [
        { "id": 42, "description": "Example methodology", "version": 1.1 }
      ]
    }
  ]
}
```

## Troubleshooting

| Symptom | Check |
|---|---|
| No HTTP request appears | Confirm `SDG_TOOL_PROJECT_API_ENABLED=true`, `local` is active, `test` is inactive, and the startup log names the REST client. |
| Connection refused | Confirm SDG Tool is listening on port `8000` and reachable from the AP backend process. |
| `404 Not Found` | Confirm the GSID exists and the SDG Tool route is `/api/projects/<GSID>/`; AP does not post to the collection-only `/api/projects` route. |
| `401` or `403` | Confirm the key is the SDG Tool API key, and that AP sends it with the `Api-Key` scheme in the `Authorization` header. |
| Credit period changes are missing | Confirm the update ran as an admin/ARM user, inspect the saved AP crediting periods, then inspect the emitted SDG payload. |
| AP update succeeds but SDG does not reflect it immediately | The external sync is asynchronous. Check backend errors and the failed-update retry records/logs. |

## References

- `back-end/src/main/java/org/goldstandard/assurance/api/controller/ProjectController.java` — AP project update route.
- `back-end/src/main/java/org/goldstandard/assurance/api/service/ProjectService.java` — applies and saves project and crediting-period updates, then schedules external sync.
- `back-end/src/main/java/org/goldstandard/assurance/api/service/external/ExternalUpdateExecutor.java` — schedules external updates.
- `back-end/src/main/java/org/goldstandard/assurance/api/service/external/sdg/SdgProjectFactory.java` — builds the SDG payload from saved project data.
- `back-end/src/main/java/org/goldstandard/assurance/api/service/external/ExternalCreditingPeriodsConverter.java` — converts saved periods and methodologies.
- `back-end/src/main/java/org/goldstandard/assurance/api/service/external/sdg/RestSdgApiClient.java` — builds the endpoint URL, headers, and HTTP POST.
- `back-end/src/main/java/org/goldstandard/assurance/api/configuration/SdgApiClientConfiguration.java` — chooses the stub or REST implementation.
- `docs/local-development-runbook.md` — local backend and local-auth setup.
