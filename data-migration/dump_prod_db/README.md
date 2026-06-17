# GS Database Dump and Restore

You need to set the following environment variables:

```bash
# Source and target environments
DUMP_AND_RESTORE_SOURCE=<environment>
DUMP_AND_RESTORE_TARGET=<environment>
```

Where `<environment>` can be one of: `local`, `dev`, `staging`, `prod`

### Database Connection Variables

For each environment you plan to use, set the corresponding database connection variables:

**Local Environment:**
```bash
LOCAL_ASSURANCE_DB_HOST=localhost
LOCAL_ASSURANCE_DB_PORT=5432
LOCAL_ASSURANCE_DB_NAME=your_db_name
LOCAL_ASSURANCE_DB_USER=your_username
LOCAL_ASSURANCE_DB_PASSWORD=your_password
```

**Dev Environment:**
```bash
DEV_ASSURANCE_DB_HOST=dev-host
DEV_ASSURANCE_DB_PORT=5432
DEV_ASSURANCE_DB_NAME=dev_db_name
DEV_ASSURANCE_DB_USER=dev_username
DEV_ASSURANCE_DB_PASSWORD=dev_password
```

**Staging Environment:**
```bash
STAGING_ASSURANCE_DB_HOST=staging-host
STAGING_ASSURANCE_DB_PORT=5432
STAGING_ASSURANCE_DB_NAME=staging_db_name
STAGING_ASSURANCE_DB_USER=staging_username
STAGING_ASSURANCE_DB_PASSWORD=staging_password
```

**Production Environment:**
```bash
PROD_ASSURANCE_DB_HOST=prod-host
PROD_ASSURANCE_DB_PORT=5432
PROD_ASSURANCE_DB_NAME=prod_db_name
PROD_ASSURANCE_DB_USER=prod_username
PROD_ASSURANCE_DB_PASSWORD=prod_password
```

## AWS Database Access

To run this locally against AWS databases, you need to establish an SSH tunnel to the relevant AWS database first.

## Usage

### Dump a database
```bash
python -m cli dump
```

### Restore a database
```bash
python -m cli restore
```

### Clear target database (removes all tables from 'gs' schema and public.databasechangelog)
```bash
python -m cli clear
```

### Restore with options
```bash
# Restore without clearing target first
python -m cli restore --no-clear-target

# Restore from specific dump file
python -m cli restore --dump-file my_dump.sql
```

## Example Workflow

1. Set environment variables for source (e.g., prod) and target (e.g., local)
2. Establish SSH tunnel to AWS database (if needed)
3. Dump from source: `python -m cli dump`
4. Restore to target: `python -m cli restore`
5. Run migrations on target db

## After running

The only thing that needs to be done manually after running, is assigning users to platform accounts as we have persisted the target db users but imported new platform accounts related to current records