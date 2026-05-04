ARG NEXUS_PREFIX=nexus.iig.caci.co.uk

# Build React static files
FROM $NEXUS_PREFIX/node:20.19.0-slim AS build
WORKDIR /app
RUN apt-get update && apt-get install -y git
COPY package*.json ./
COPY . .
COPY .git .git
RUN npm install
RUN npm run build

# Install nginx, copy build files and server files, npm install
FROM $NEXUS_PREFIX/node:20.19.0-slim
WORKDIR /app

RUN apt-get update && apt-get install -y nginx gettext

COPY --from=build /app/dist /usr/share/nginx/html
COPY ./src/express /app/express
COPY package*.json ./

RUN npm install --omit=dev

COPY nginx.conf /etc/nginx/sites-available/default.template
COPY /ci/entrypoint.sh /app/entrypoint.sh

RUN chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]
