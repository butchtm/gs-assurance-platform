#!/usr/bin/env bash

EXEDIR=$(dirname $0)

if [ -f ${EXEDIR}/.env ]; then
  source ${EXEDIR}/.env
elif [ -f ${EXEDIR}/../.env ]; then
  source ${EXEDIR}/../.env
fi

env | sort 

python3 $(dirname $0)/dummy_documents.py \
  --sa="${STORAGE_ACCOUNT_URL}" \
  --srccontainer="${STORAGE_ACCOUNT_CONTAINER}" \
  --destbucket="${DEST_S3_BUCKET}"
