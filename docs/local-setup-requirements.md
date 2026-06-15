Local setup requirements - Assurance Platform (cross-linking feature)

Must-haves:

1. Data-migration repository (Liquibase): full snapshot of the separate data-migration repo. The local docker-compose expects it as a sibling folder named "data-migration". Also: does the Liquibase "local" context already include seed/sample data? If not, please include a sanitized data dump or seed scripts.

2. Latest repository snapshot: our current copy is from May 4. We'd prefer the latest (Postgres upgrade and CVE fix); otherwise please confirm May 4 is acceptable for the cross-linking scope.

3. AWS Cognito configuration values (no console/infra access needed):
   - Dev user pool ID
   - App client ID
   - Test users for Admin, ARM, and Project Developer roles (or just their Cognito subs)

4. Environment variables / local config: the .env and environment values CACI developers use for local development (sanitized of secrets is fine).

Nice-to-haves:

5. Docker-compose modifications Gavin made to the front-end and data-migration repos for local setup (diff or copy).

6. Local development setup docs: export (PDF or markdown) of any "getting started locally" Confluence pages, since we won't have GitLab/Confluence access.

7. Authoritative backend confirmation: "back-end" vs "back-end-spring-boot-4-upgrade", which is current main, and which branch/tag matches production?

8. Known local build issues/workarounds, e.g. the CVE encountered when first building one of the repos.

9. Contact channel for follow-up questions with the volunteer developer.
