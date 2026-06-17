# Assurance Database Migrations

[Liquibase](https://www.liquibase.org/) is used to perform the following activities against the Assurance Database:
* initial schema creation
* staging data from the source Azure SQLServer database
* data sanitisation
* schema changes
* reference data population

Broadly, Liquibase applies a sequence of collections of changes (Data Manipulation Language (DML), Data Definition Language (DDL), and/or declarative database-agnostic changes) to a specified target database; Liquibase tracks what changes have already been applied through a pair of control tables.

This directory contains:
* `changelog.yaml` - the sequence of change-sets (collections of changes)
* `*.sql` - the individual change-sets; at the time of writing we use PostgreSQL SQL exclusively.

## Liquibase connection properties
Database connection properties and Liquibase-specific properties can be provided in a properties file, as environment variables, and/or command line arguments. We provide a basic template, `liquibase.properties.TEMPLATE`, which should be copied to `liquibase.properties` and amended accordingly for your specific database target.

## Running Liquibase as a container
This is the simplest way to run Liquibase locally because the [official image](https://hub.docker.com/r/liquibase/liquibase) already contains the requisite PostgreSQL driver.

Note that if using the container to target a locally hosted database, the database host specified must be a host IP address that is reachable by the container, and not localhost.

To run the migration scripts in a liquibase container pointing at a postgres db running on your local 
machine is shown below (note the volume mount path will need to be changed depending on where you have it
checked out on your local machine.

```bash
docker run \
--rm \
-v {path-to-data-migration-project}/data-migration/migrations:/liquibase/changelog \
liquibase/liquibase \
--url="jdbc:postgresql://host.docker.internal:5432/gs" \
--username=postgres \
--password=MySuperSafePostgresPassword \
--changeLogFile=changelog.yaml \
--searchPath=./changelog \
--contexts=local \
update
```

## Installing Liquibase locally
1. Install [`Liquibase`](https://www.liquibase.com/download)
2. Install [`PostgreSQL JDBC driver`](https://jdbc.postgresql.org/)
3. Copy `liquibase.properties.TEMPLATE` to `liquibase.properties` and amend the properties for your environment

### Liquibase Wrappers

There are a few wrappers to assist with running Liquibase locally

| Wrapper | for command | Notes |
| ------- | ----------- | ----- |
| `lb_update` | liquibase update | apply db updates |
| `lb_update_sql` | liquibase update-sql | a 'dry-run' |
| `lb_update_to_tag MY_TAG` | liquibase update-tag | apply db updates up to specified tag |
| `lb_update_to_tag_sql MY_TAG` | liquibase update-tag-sql | a 'dry-run' up to specified tag |


## Running migrations
We use Liquibase contexts and context filters to apply selected scripts to specific environments only.
e.g. data sanitisation is applied to all environments other than `production` and `staging`

From the migrations (this) directory, run 

e.g.

```
CONTEXTS='dev' ./lb_update
```
This is just a thin wrapper around `liquibase` cli.
You should see output similar to the following:
```
####################################################
##   _     _             _ _                      ##
##  | |   (_)           (_) |                     ##
##  | |    _  __ _ _   _ _| |__   __ _ ___  ___   ##
##  | |   | |/ _` | | | | | '_ \ / _` / __|/ _ \  ##
##  | |___| | (_| | |_| | | |_) | (_| \__ \  __/  ##
##  \_____/_|\__, |\__,_|_|_.__/ \__,_|___/\___|  ##
##              | |                               ##
##              |_|                               ##
##                                                ## 
##  Get documentation at docs.liquibase.com       ##
##  Get certified courses at learn.liquibase.com  ## 
##                                                ##
####################################################
Starting Liquibase at 15:29:47 (version 4.23.2 #13458 built at 2023-09-11 22:00+0000)
Liquibase Version: 4.23.2
Liquibase Open Source 4.23.2 by Liquibase
Running Changeset: changelog.yaml::000010-create-schema::sladem
Running Changeset: changelog.yaml::000020-reference-data::sladem
Running Changeset: changelog.yaml::000030-project-tables::sladem
Running Changeset: changelog.yaml::000040-load-project-data::sladem

UPDATE SUMMARY
Run:                          4
Previously run:               0
Filtered out:                 0
-------------------------------
Total change sets:            4

Liquibase: Update has been successful. Rows affected: 34798
Liquibase command 'update' was executed successfully.
```

## Cleardown
There is a sql script `reset.sql` to remove 
* foreign tables schema
* staging schema
* gs schema
* gs data quality schema
* and the liquibase control tables

e.g.

`psql -U postgres -h 127.0.0.1 -W -f reset.sql`


## Postgres Foreign Data Wrapper setup
[`tds_fdw` (foreign data wrapper for SQL Server data)](https://github.com/tds-fdw/tds_fdw) is supported for Aurora PostgreSQL - [see here](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/Appendix.PostgreSQL.CommonDBATasks.Extensions.foreign-data-wrappers.html#postgresql-tds-fdw).

Build and installation instructions for `tds_fdw` can be found on the [project website](https://github.com/tds-fdw/tds_fdw)

Note that FreeTDS uses a `locales.conf` (typically `/etc/locales.conf`) to control timestamp formatting. The default timestamp format doesn't play well with Postgres so the following default locale format is suggested:
```
[default]
        date format = %Y-%m-%d %H:%M:%S.%z
```

Once FreeTDS has been installed, configured, and tds_fdw built/installed, the foreign data for the SQL Server db can be setup:
```
CREATE EXTENSION tds_fdw;  -- needs to be done once per PostgreSQL cluster

CREATE SERVER sqlserver FOREIGN DATA WRAPPER tds_fdw OPTIONS (
  servername 'TBD',
  port '1433',
  database 'TBD',
  character_set 'UTF-8'
);

GRANT USAGE ON FOREIGN SERVER sqlserver TO postgres;

CREATE USER MAPPING FOR postgres SERVER sqlserver OPTIONS (username 'TBD', password 'TBD');

CREATE SCHEMA sqlserver_schema;

--
--Not required unless the foreign schema changes - we have the foreign-table definitions captured in 000005-foreign-tables.sql
--
--IMPORT FOREIGN SCHEMA dbo
--EXCEPT (list_of_tables_to_exclude)
--FROM SERVER sqlserver
--INTO sqlserver_schema
--OPTIONS (import_default 'true', import_not_null 'true');
```

Once this SQL has been run, it should be possible to query the foreign tables

e.g.

`select * from sqlserver_schema.mytable;`

## Liquibase Contexts

| Context | Purpose | Effect |
| ------- | ------- | ------ |
| `good-tds-fdw` | Set if PostgreSQL has working TDS FDW | Foreign table and staged data schemas created and populated |
| `staging` | Set if staging environment | No data sanitisation |
| `production` | Set if production environment | No data sanitisation |
| neither `staging` nor `production` | | Data sanitised |

e.g.

```
CONTEXTS=good-tds-fdw,staging ./lb_update_to_tag staged-data
```

```
####################################################
##   _     _             _ _                      ##
##  | |   (_)           (_) |                     ##
##  | |    _  __ _ _   _ _| |__   __ _ ___  ___   ##
##  | |   | |/ _` | | | | | '_ \ / _` / __|/ _ \  ##
##  | |___| | (_| | |_| | | |_) | (_| \__ \  __/  ##
##  \_____/_|\__, |\__,_|_|_.__/ \__,_|___/\___|  ##
##              | |                               ##
##              |_|                               ##
##                                                ## 
##  Get documentation at docs.liquibase.com       ##
##  Get certified courses at learn.liquibase.com  ## 
##                                                ##
####################################################
Starting Liquibase at 09:45:24 (version 4.23.2 #13458 built at 2023-09-11 22:00+0000)
Liquibase Version: 4.23.2
Liquibase Open Source 4.23.2 by Liquibase
Running Changeset: changelog.yaml::000005-foreign-tables::sladem
Running Changeset: changelog.yaml::000010-build-and-populate-staging::sladem
Running Changeset: changelog.yaml::000028-tag-staged-data::sladem

UPDATE SUMMARY
Run:                          3
Previously run:               0
Filtered out:                20
-------------------------------
Total change sets:           23


FILTERED CHANGE SETS SUMMARY

Context mismatch:             2
After tag:                   18

Liquibase: Update-to-tag has been successful. Rows affected: 839371
Liquibase command 'update-to-tag' was executed successfully.
```

## Liquibase Tags

Tags enable updates to be applied up to the specified tag

| Tag | Description |
| --- | ----------- |
| `staged-data` | Ingest raw data (sanitised depending on context) to staging db |
| `v0.1.0` | First cut of the migrated assurance data |

e.g. see above