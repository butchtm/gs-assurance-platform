# Assurance Platform Data Migration

[Gold Standard](https://www.goldstandard.org/) Assurance Platform data is migrated from their extract of [SustainCERT](https://www.sustain-cert.com/) data:

| Source                                       | Destination        | Content                                               | Tooling                                                                                                                        |
| -------------------------------------------- | ------------------ | ----------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------ |
| Azure SQLServer database, `scdata1copy1`     | AWS PostgreSQL RDS | climate and sustainable development project data      | [Liquibase](https://www.liquibase.org/), [PostgreSQL foreign tables](https://www.postgresql.org/docs/15/ddl-foreign-data.html) |
| Azure Blob storage container, `scdata230411` | AWS S3 bucket      | climate and sustainable development project documents | [rclone](https://rclone.org/)                                                                                                  |

## Directories

| Directory        | Purpose                                             |
| ---------------- | --------------------------------------------------- |
| `gitlab_runners` | Dockerfile to build bespoke Liquibase GitLab runner |
| `migrations`     | Liquibase database migrations                       |
| `utils`          | Various utility scripts                             |

## Database Migration

There are 3 high level activities to migrate the data into the Assurance Platform:

- [Prepare the target database](migrations/MIGRATION.md#prepare-the-target-database)
- [Stage (and optionally sanitise) the source data](migrations/MIGRATION.md#stage-the-source-data)
- [Perform the migration](migrations/MIGRATION.md#perform-the-migration)

## Document Migration

[Running document migration](DOCUMENT_MIGRATION.md#running-document-migration)

## Running locally

By installing Postgres and Liquibase on Windows locally, this migration can be run via the command:
`liquibase --url="jdbc:postgresql://localhost:5432/assurance_db" --changeLogFile=changelog.yaml --username=<username> --password=<password> --searchPath=./migrations --contexts="local" update`

### Checksum errors

These occur if you attempt to rerun changelog scripts. For example, if you had to make amendments before committing work.

These can be cleared like so:
`liquibase --url="jdbc:postgresql://localhost:5432/assurance_db"  --username=<username> --password=<password> --contexts="local" clear-checksums`
