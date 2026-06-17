#!/bin/bash

PGPASSWORD=${POSTGRES_PASSWORD} pg_dump \
  --username=postgres \
  --dbname=assurance_db \
  --schema=staging \
  --file=/tmp/assurance-db-staging-schema-${ENVIRONMENT}.dump.$(date +%Y%m%d).sql