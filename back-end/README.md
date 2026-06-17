# Gold Standard Assurance API Service

[![pipeline status](https://git.iig.caci.co.uk/gold-standard/back-end/badges/main/pipeline.svg)](https://git.iig.caci.co.uk/gold-standard/back-end/-/commits/main)
[![Quality Gate Status](https://sonarqube.iig.caci.co.uk/api/project_badges/measure?project=gold-standard-assurance-backend&metric=alert_status)](https://sonarqube.iig.caci.co.uk/dashboard?id=gold-standard-assurance-backend)
[![Reliability Rating](https://sonarqube.iig.caci.co.uk/api/project_badges/measure?project=gold-standard-assurance-backend&metric=reliability_rating)](https://sonarqube.iig.caci.co.uk/dashboard?id=gold-standard-assurance-backend)
[![Coverage](https://sonarqube.iig.caci.co.uk/api/project_badges/measure?project=gold-standard-assurance-backend&metric=coverage)](https://sonarqube.iig.caci.co.uk/dashboard?id=gold-standard-assurance-backend)
[![Code Smells](https://sonarqube.iig.caci.co.uk/api/project_badges/measure?project=gold-standard-assurance-backend&metric=code_smells)](https://sonarqube.iig.caci.co.uk/dashboard?id=gold-standard-assurance-backend)
[![Technical Debt](https://sonarqube.iig.caci.co.uk/api/project_badges/measure?project=gold-standard-assurance-backend&metric=sqale_index)](https://sonarqube.iig.caci.co.uk/dashboard?id=gold-standard-assurance-backend)

Provides a Spring Boot based API application for the Gold Standard Assurance service

# Local Development Quickstart

The following are prerequisites:
- [Maven](https://maven.apache.org/install.html#apt)
- [Docker](https://docs.docker.com/engine/install/ubuntu/#install-using-the-repository)
- [SDKMan](https://sdkman.io/install/)

## Java Version

This repo requires the use of Java 21. We use SDKMan to switch between versions 
of Java when required. Specifically, we follow the spring boot maven plugin and 
use Java 21.0.8 of the [Bellsoft Liberica JDK](https://bell-sw.com/libericajdk/).
To install this version, run the following command from the root directory of
this repository:

```bash
sdk env install
```

This sets the java version based on the contents of `.sdkmanrc` located in the 
root of this project. Additionally, edit `~/.sdkman/etc/config` to be:
```
sdkman_auto_env=true
```
and SDKMan will automatically set the java version when you navigate to the root
directory of this project.


Now the `java -version` command should give this output:

```bash
openjdk version "21.0.8" 2025-07-15 LTS
OpenJDK Runtime Environment (build 21.0.8+12-LTS)
OpenJDK 64-Bit Server VM (build 21.0.8+12-LTS, mixed mode, sharing)
```

## Database setup

Clone the [data migration](https://git.iig.caci.co.uk/gold-standard/data-migration)
repo so that it is a sibling of this one.

Run this command to setup a container running the database:
```
docker compose -f local/compose.yml up --build -d
```

## Setup env variables to use Development Cognito

The Assurance service uses AWS Cognito to manage authentication. For local development, we make use of the Cognito User pool from the Development environment. In order to do so, we need to acquire some keys and ids from the Development Environment.

This information can only be obtained by a user with the `AdministratorAccess` role. You can request this role from any user who is an admin on the `GS-Assurance` account. That individual should follow the process outlined in this [document](https://goldstandardfoundation.atlassian.net/wiki/x/AwDtVQ#Adding-new-users-to-IAM-Access-Management-for-S3-bucket-and-other-access).

Once that has been done, follow this [document](https://goldstandardfoundation.atlassian.net/wiki/x/AoDJVw) to acquire the relevant data. The values can then be set up as environment variables like this:

```bash
export AWS_ACCESS_KEY_ID={aws-access-key-id}
export AWS_SECRET_ACCESS_KEY={aws-secret-access-key}
export AWS_SESSION_TOKEN={aws-session-token}
export COGNITO_USER_POOL_ID={user-pool-id}
export COGNITO_APP_CLIENT_ID={app-client-id}
```

## Run the back-end application

Run this command:

```bash
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

If successful, you should see something like:
```
12:49:07   CSD013014-LW10  INFO [main] o.g.assurance.api.AssuranceApplication   : Started AssuranceApplication in 14.663 seconds (process running for 14.965)
```

## Next steps

Follow the [front-end](https://git.iig.caci.co.uk/gold-standard/front-end) quickstart to setup the front end.

# Spring Boot Features

## Spring Boot Actuator

Health endpoint is provided
by [spring-boot-actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html).

The health endpoint is available at http://localhost:8080/actuator/health.
All other default endpoints provided by actuator are disabled for security reasons, although we could open them if
useful.

## Spring Security

This project is configured to expect JWTs from an OIDC compliant server. Simply set the correct options in the
application.yml
For testing purposes a local public/private key pair are used to generate and verify JWTs using the same mechanisms.
See the SecurityConfiguration class for security implementation details.

When deployed the following environment variable is set to identify the OAuth2 server.<br>
`SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUERURI=https://cognito-idp.eu-central-1.amazonaws.com/eu-central-1_zzzzzzzzz`

### Permissions

Role base access control is defined at each controller where required. For finer access control the permissions service
provides methods for returning if the requesting user can access a specific project/account.

A PublicController provides public variants of secured endpoints.

## User Synchronisation

The UserSyncService periodically synchronises user data from AWS Cognito via the CognitoUSerService to the
Gold Standard database user table. Synchronisation is carried out periodically according to the cron schedule specified
in application.yml or overridden by environment variable e.g.<br>
`USER_SYNC_CHECK_CRON=0 */5 * * * *`<br>
If multiple backends are deployed, each instance will check whether a sync should run according to the
USER_SYNC_CHECK_CRON schedule. e.g.<br>
`USER_SYNC_SYNC_INTERVAL=900`
A user sync will only be run, however, if the last sync ran at least USER_SYNC_SYNC_INTERVAL seconds ago.

## AWS SDK

Application uses the AWS SDK to provide access to AWS resources such as S3 storage and Cognito. See
S3ClientConfiguration and CognitoClientConfiguration for client implementation details.
The S3 bucket name is defined in application.yml and can be overridden by an environment variable e.g.<br>
`S3_BUCKET_NAME=<new-bucket-name>`

Cognito properties are defined in application.yml and can be overridden by environment variables e.g.<br>
`COGNITO_REGION_NAME=<region>`<br>
`COGNITO_USER_POOL_ID=<cognito pool id>`<br>

To run locally AWS credentials also need to be provided, these can be added as environment variables e.g.<BR>
`AWS_ACCESS_KEY_ID=<access key>`<br>
`AWS_SECRET_ACCESS_KEY=<secret access key>`<br>
`AWS_SESSION_TOKEN=<session token>`<br>
See https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials-chain.html for more info.

## Building as a Docker Image

Simply run `mvn spring-boot:build-image -dcustom.image.name=my-assurance-api`

Then to start run `docker run -p 8080:8080 my-assurance-api:latest`

# Local Development

## Managing the Java version

This usage [guide](https://sdkman.io/usage/) provides more details on SDKman commands.

## Setting up a database

To run locally you should have a Postgres instance.
You can use the Docker Compose configuration
in [the `./local` subdirectory](./local).
Using the `compose.yaml` file **requires** that you have checked out the
[`data-migration`](https://git.iig.caci.co.uk/gold-standard/data-migration)
repository alongside this `back-end` repository.
The `data-migration` repository contains database migration scripts
that are used by Liquibase to set up the local database schema.

From the root directory of this project you can run the following command:

```bash
docker compose -f local/compose.yml up --build -d
```

If more fine-grained control is required,
it is possible to issue individual commands to Liquibase
by overriding the Docker container command.

```bash
# if in the directory, the configuration is picked up automatically
cd local
# manually validate the changelog
docker compose run --rm liquibase validate
# check the SQL that will be executed by an "update" prior to executing
docker compose run --rm liquibase update-sql
# manually execute an "update"
docker compose run --rm liquibase update
# check the SQL that will be executed by a "rollback-count" prior to executing
docker compose run --rm liquibase rollback-count-sql --count=1
# manually execute a "rollback-count"
docker compose run --rm liquibase rollback-count --count=1
# if you forget to include "--rm", remember to clean up your stopped containers
```

These might be useful for locally verifying a migration
prior to deploying to the Development environment.

Note that some parameters are configured via environment variables
in the Docker Compose configuration.
So to change the Liquibase changeset context,
the `compose.yaml` configuration may need to be modified.

## With Development Cognito to work with a local front-end

Once the database is running, the back-end application can be run locally pointing at Cognito in
the development AWS account by setting up environment variables to point at the Cognito user pool
by running the following:

```bash
export AWS_ACCESS_KEY_ID={aws-access-key-id}
export AWS_SECRET_ACCESS_KEY={aws-secret-access-key}
export AWS_SESSION_TOKEN={aws-session-token}
export COGNITO_USER_POOL_ID={user-pool-id}
export COGNITO_APP_CLIENT_ID={app-client-id}
```

Then providing the local spring profile from the command line by running, the command below.

```bash
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

With the API up and running pointing at Development cognito you will be able to run the
[front-end](https://git.iig.caci.co.uk/gold-standard/front-end) pointing at the `back-end` instance running on your
local machine.

## Accessing AWS S3 buckets from local

### Downloading

Reading from S3 buckets to download existing documents can be done by configuring
the AWS access keys for the environment you want to connect to, you can do this by
running:

```bash
export AWS_ACCESS_KEY_ID={access-key-id}
export AWS_SECRET_ACCESS_KEY={secret-access-key}
```

### Uploading

The local profile will stub out interactions that upload documents to S3 buckets by
default, but this can be altered. The application uploads to S3 through the `DocumentUploader`
interface which acts as an adapter. There are two implementations of this interface
`DocumentUploaderDefault` which is configured to run for all profiles except `local` and
`DocumentUploaderStubbed` which runs specifically for the `local` profile. In order to access
S3 buckets from local you will need to have your VPN running, and manually "swap" the `@Profile` annotations on the
stubbed and default implementations of the `DocumentUploader` interface so that the default
one is used locally (NOTE - remember to revert these changes when you are done so they do
not get committed to the repo!) once this change is made if you restart your backend application, having
also set the AWS secret key and access key as required for downloading then you will be
able to upload documents to the development S3 bucket from your local API.

## Sending emails via AWS SES from local

The process here is very similar to the process outlined for uploading documents to
S3 above. Again you need to set the AWS access keys.

```bash
export AWS_ACCESS_KEY_ID={access-key-id}
export AWS_SECRET_ACCESS_KEY={secret-access-key}
```

For emails the adapter interface is `EmailSender` which has the default implementation
`SesEmailSender` and the stubbed implementation `StubEmailSender`. As above, the profiles
just need to be switched before the service is started (NOTE - remember to revert these
changes when you are done so they do not get committed to the repo!)

We also have the `ContactMappingService` which is used to map email addresses in scenarios
where we may not want to send emails to "real" addresses in non production environments, so
you may also need to update the contact mapping configuration to ensure you emails will be
sent as required, if the contact mappings service is overriding or stopping emails being
sent then warning messages should be written to the application logs.

### Uploading

The local profile will stub out interactions that upload documents to S3 buckets by
default, but this can be altered. The application uploads to S3 through the `DocumentUploader`
interface which acts as an adapter. There are two implementations of this interface
`DocumentUploaderDefault` which is configured to run for all profiles except `local` and
`DocumentUploaderStubbed` which runs specifically for the `local` profile. In order to access
S3 buckets from local you will need to have your VPN running, and manually "swap" the `@Profile` annotations on the
stubbed and default implementations of the `DocumentUploader` interface so that the default
one is used locally (NOTE - remember not to revert these changes when you are done so they do
not get committed!) once this change is made if you restart your backend application, having
also set the AWS secret key and access key as required for downloading then you will be
able to upload documents to the development S3 bucket from your local API.

## Without Development Cognito to run API standalone

The same command can be used to run the test profile for the backend application, although
using this method will not work with the `front-end` running locally.

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=test"
```

You can then get a token using the /token endpoint and pass this in the auth header as a bearer token for all requests.

Example curl command for getting a locally generated Admin token:<br>
```TOKEN=$(curl -s "http://127.0.0.1:8080/api/token?role=GS-Admin")```

Example curl command for getting the projects list using the above generated token:<br>
```
curl -v POST http://127.0.0.1:8080/api/projects -H 'Authorization: Bearer '$TOKEN'' | json_pp
```

# Testing

The *APITest tests start up the application on a random web port and send HTTP Requests to check the endpoints are
configured correctly.

The spring security is active on the test, the application runs in 'local' mode which activates a local key-pair for
issuing and validating tokens.

# Linting

This project uses Google-Java-Format and spotless to lint.

You can run `mvn spotless:apply` to auto-lint all files.
`mvn clean compile` can be used to validate linting/build

### IntelliJ

If using IntelliJ go to File > Settings > Plugins and download google-java-format Plugin and restart IntelliJ when asked.
After restarting, CTRL+ALT+L or CTRL+ALT+SHIFT+L can be used to lint/lint with options.

Google java format replaces IntelliJ's default linting behaviour and can also be configured to run on save.

# Deployment

The gitlab pipeline is currently configured to push a new build to AWS ECR when a merge to main occurs, note that the
new build is not automatically deployed so will require manually redeploying.
To manually redeploy:

- Go to Amazon Elastic Container Services
- Select the development cluster
- Select the api development service
- Select the Update Service button
- Tick Force new deployment
- Press the Update button
- Click into the Tasks view and watch as the new one comes up and the old goes down

The AWS Application is configured via terraform, see the gold standard infrastructure project, see modules/ecs/ecs.tf

## Viewing the OpenAPI spec locally

Once you have the application running locally from following any of the sets of commands above, you can view
the OpenApi spec for the application with the following curl command, replacing the `{your-token}` placeholder
with a valid bearer token:

```bash
curl --request GET \
  --url http://localhost:8080/api/v3/api-docs \
  --header 'Authorization: Bearer {your-token}'
```~~

