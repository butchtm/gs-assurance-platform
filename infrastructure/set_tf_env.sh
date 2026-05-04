#!/bin/sh
BASEDIR=$(dirname $0)
rm -rf ${BASEDIR}/.terraform && envsubst '${GS_ENV_NAME}' < ${BASEDIR}/providers.tf.template > ${BASEDIR}/providers.tf
cat ${BASEDIR}/providers.tf