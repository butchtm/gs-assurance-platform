# Database Migration

There are 3 high level activities to migrate the data into the Assurance Platform:
* [Prepare the target database](#prepare-the-target-database)
* [Stage (and optionally sanitise) the source data](#stage-the-source-data)
* [Perform the migration](#perform-the-migration)

## Connecting to the database

The below steps require the creation of an SSH tunnel to the database via our bastion EC2 instance. When prompted follow these steps.
To do this you will need the psql client installed on your local machine.

When I tried this initially myself I got an error that the postgres client I had
installed locally was out of date, so to get a later version (in my case version 16) 
I ran the following commands:

```bash
sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'
wget -qO- https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo tee /etc/apt/trusted.gpg.d/pgdg.asc &>/dev/null
sudo apt update
sudo apt install postgresql
```

To be able to connect to the target remote environment you will
need to set up an ssh tunnel onto the RDS instance where you want to import the data.
To do this you will need to run the following:

```bash
eval $(ssh-agent -s)
ssh-add <(echo "${BASTION_PRIVATE_KEY}" | base64 -d)
ssh -o StrictHostKeyChecking=no \
    -f \
    -N \
    -p ${BASTION_PORT} \
    -L 127.0.0.1:15432:${DB_HOST}:5432 \
    ${BASTION_USERNAME}@${BASTION_HOST}
```
**Note** - the bastion host, port, username and db host variables are stored in the gitlab
pipeline variable configuration, so you can grab the actual values from there as required.


## Prepare the target database

**Note** - when running locally the target database is prepared for you automatically by scripts
that are run and configured by docker-compose. You will only need to refer to the infrastructure
project and perform the command below if you are setting up a brand-new environment and rds cluster.
If you are running a migration to refresh an existing environment you can skip these steps and move
onto staging the source data.

The [Infrastructure project](https://git.iig.caci.co.uk/gold-standard/infrastructure) provisions the requisite Assurance Platform infrastructure.
including an RDS PostgreSQL database cluster, to a given environment (development, staging, production).

Once the database cluster has been provisioned, you need to create the Assurance Platform database. 

First follow the [connecting to the database](#connecting-to-the-database) instructions.
You will then need to connect to the default database `postgres` created by RDS, when prompted enter the password `postgres`.
```
psql --host=127.0.0.1 --port=15432 --username=assurance_dbadmin  --dbname=postgres --password
```

Once connected to the database run the following.

```sql
CREATE DATABASE "assurance_db"
WITH
  ENCODING = 'UTF8'
  LC_COLLATE = 'en_GB.UTF-8'
  LC_CTYPE = 'en_GB.UTF-8'
  CONNECTION LIMIT = -1
  TEMPLATE "template0";
```

This is a one time activity when a new environment is set up, so if you are migrating into
and existing environment, this step will not be required.

## Stage the source data

The first step to running the migration is to pull all the data from
the `scdatacopy` database into the staging database locally which can
then be exported from a local container and into the target database
i.e. the development or staging environment.

The creation of the local staging database is performed using docker
compose. It requires credentials for the sustain cert `scdatacopy`
database to be populated into the env file at `local/.env` The file
expects the following variables to be set:

```.env
SC_DB_USERNAME=<db-username>
SC_DB_PASSWORD=<db-password>
ENVIRONMENT=development
```

The username and password are not committed to the repo for obvious
reasons, you will need to request these from another team member or
from sustain cert directly. The `ENVIRONMENT` variable will control
whether the data is sanitised before populating the staging database,
and should be set to either `development`, `staging` or `production`
respectively.

Once you have set up the values in the `local/.env` file you can spin
up the staged database by running:

```bash
docker compose -f local/compose.yml up -d --build
```

Note - the `--build` argument is only required when first attempting to
run the migration, it will force the db container image to be rebuilt
using the config in this repo. You might run into issues around the
`assurance_db` database schema not being present or found if you omit
this argument.

This will create two containers one with the database itself and one
with liquibase to run the migrations. The liquibase container will eventually
stop once the migrations have completed, to view the migration logs you can
run the following command:

```bash
docker logs local-liquibase-1 -f
```

This will follow the logs until they complete, which will hang for a while
whilst the data is being pulled across. Once that process has completed you
can a pg dump that will extract the staged database ready to be imported into
the target environment databases i.e. development, staging and production by
running the following commands:

```bash
docker exec -it local-db-1 /bin/bash
./home/run-dump.sh
```

Once this has run a dump of the staging database will be produced on your local
machine in the `local/dump` folder and will have the name format:
`assurance-db-staging-schema-{ENV}.dump.{DATE}.sql`

Now that you have the staged source data extracted into a postgres dump, it
is ready to be imported into the target remote environment RDS postgres database.

**Follow the instructions** under [connecting to the database](#connecting-to-the-database) to create an SSH tunnel (if not already).

**Note** - if this is not a clean environment and the database already exists then
you will need to delete some the existing schemas and tables before running the import
command, to do this you can connect to the db on the command line directly by running:

```bash
export PGPASSWORD='{password}'
psql --host=127.0.0.1 \
  --port=15432 \
  --username=assurance_dbadmin \
  --dbname=assurance_db
```

**Note** - the single quotes around the password value are required

and then at the psql prompt running:

```sql
drop schema staging cascade;
drop schema gs_dq cascade;
drop schema gs cascade;
drop table public.databasechangelog;
drop table public.databasechangeloglock;
```

Once these have been dropped you should be able to run the psql command with the
dump file argument again to import the staged data. If it is a new / empty
environment you are setting up then the drop commands will not be necessary.

With the ssh tunnel running you will now be able to import the database dump by running
the following:

```bash
psql --host=127.0.0.1 \
  --port=15432 \
  --username=assurance_dbadmin \
  --dbname=assurance_db \
  --password \
  --file=local/dump/assurance-db-staging-schema-{ENV}.dump.{DATE}.sql
```

**Note** - this will prompt for a database password, which as above you can grab from
the gitlab pipeline variables as required.

## Perform the migration

Once you have followed the steps above to populate the staged data into an
environment, you can run the migrations to complete the database set up.

There are [scheduled jobs in GitLab](https://git.iig.caci.co.uk/gold-standard/data-migration/-/pipeline_schedules) to perform dry runs and actual migrations
against each of the 3 environments, development, staging, and production. All 
the migrations run off `main` branch.

* The `Dry-run migrations` schedule for a given environment will output the SQL that
represents all the migrations yet to be applied to that environment.
* The `Apply migrations` schedule for a given environment applies all outstanding
migrations to that environment.
* The `Discrepancy checks` schedule for a given environment will check for any
differences between the SC data and the migrated data in the assurance platform,
used to confirm that data has been migrated successfully. **Note** in development
and staging it is likely that discrepancies will be flagged for obfuscated names
and email addresses.

If this is a new environment, you then need to set the password of the `assurance_backend` user that the migrations have created.
This is the user the back-end API uses to connect to the database.

Once the migrations are complete you will need to [connect to the database](#connecting-to-the-database) again
and re-run the following command to enter the database with the master user details.

```bash
export PGPASSWORD='{password}'
psql --host=127.0.0.1 \
  --port=15432 \
  --username=assurance_dbadmin \
  --dbname=assurance_db
```

The password can be found in the AWS console in Secrets Manager and should be called `${ENVIRONMENT}/assurance_db_backend`.

```sql
ALTER USER assurance_backend WITH PASSWORD 'password';
```