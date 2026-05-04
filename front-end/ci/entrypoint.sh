#!/bin/bash

if [ -z "$PROXY_TARGET" ]; then
  export PROXY_TARGET=http://localhost:8080
fi

# Substitute environment variables in nginx.conf
envsubst '${PROXY_TARGET}' < /etc/nginx/sites-available/default.template > /etc/nginx/sites-available/default

# Substitute environment variables in application code
filename=$(ls -t /usr/share/nginx/html/assets/env-*.js | head -n1)
echo "performing envsubst on $filename"
cat $filename | envsubst '${VITE_ENVIRONMENT}' > $filename.temp && mv $filename.temp $filename

node /app/express/server.js &

nginx -g "daemon off;"
