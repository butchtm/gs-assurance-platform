# Gold Standard Assurance API Service

[![pipeline status](https://git.iig.caci.co.uk/gold-standard/back-end/badges/main/pipeline.svg)](https://git.iig.caci.co.uk/gold-standard/back-end/-/commits/main)
[![Quality Gate Status](https://sonarqube.iig.caci.co.uk/api/project_badges/measure?project=gold-standard-assurance-backend&metric=alert_status)](https://sonarqube.iig.caci.co.uk/dashboard?id=gold-standard-assurance-backend)
[![Reliability Rating](https://sonarqube.iig.caci.co.uk/api/project_badges/measure?project=gold-standard-assurance-backend&metric=reliability_rating)](https://sonarqube.iig.caci.co.uk/dashboard?id=gold-standard-assurance-backend)
[![Coverage](https://sonarqube.iig.caci.co.uk/api/project_badges/measure?project=gold-standard-assurance-backend&metric=coverage)](https://sonarqube.iig.caci.co.uk/dashboard?id=gold-standard-assurance-backend)
[![Code Smells](https://sonarqube.iig.caci.co.uk/api/project_badges/measure?project=gold-standard-assurance-backend&metric=code_smells)](https://sonarqube.iig.caci.co.uk/dashboard?id=gold-standard-assurance-backend)
[![Technical Debt](https://sonarqube.iig.caci.co.uk/api/project_badges/measure?project=gold-standard-assurance-backend&metric=sqale_index)](https://sonarqube.iig.caci.co.uk/dashboard?id=gold-standard-assurance-backend)

Provides a Spring Boot based API application for the Gold Standard Assurance service

# Getting Started

Install:

- Java 21 (preferably managed by SDKMan or equivalent as described below)
- Maven
- Docker

## Java Version

This repo requires the use of Java 21. If you also work with other repos that required different versions of Java,
[SDKMan](https://sdkman.io/) is a useful tool for enabling you to switch between version of Java
(or other Java SDK based languages) easily when required.

The local build for this repo has been tested using Java 21.0.8 of the [Bellsoft Liberica JDK](https://bell-sw.com/libericajdk/)
(as this is the default JDK used by the spring boot maven plugin) which can be set up by running the following command
once you have SDKMan installed.

```bash
sdk env install
```

This will set the java version based on the config defined in the .sdkmanrc file at the root of this project.
If you also update the file at ~/.sdkman/etc/config to contain sdkman_auto_env=true then each time you
navigate into the root project directory in a terminal sdk man will auto set the java version for you.

Alternatively if you would rather manually configure your sdk versions using sdk man you can use the following commands

```bash
sdk install java 21.0.8-librca
sdk use java 21.0.8-librca
```

Note - the second command is only required if you do not choose to make the Java 21 Liberica version your default JVM
when installing.

Once this is set up it will give the following output when the `java -version` command is run:

```bash
openjdk version "21.0.8" 2025-07-15 LTS
OpenJDK Runtime Environment (build 21.0.8+12-LTS)
OpenJDK 64-Bit Server VM (build 21.0.8+12-LTS, mixed mode, sharing)
```

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

## Setting up a database

To run locally you should have a Postgres instance. You can use the docker compose script in the local folder. Using
the compose file requires that you have changed out the
[data migration](https://git.iig.caci.co.uk/gold-standard/data-migration) repo alongside this `back-end` repo as it
will use the migration scripts from that repo to set up your local database schema.

From the root directory of this project you can run the following command:

```bash
docker-compose -f local/compose.yml up -d
```

## With DEV Cognito to work with a local front-end

Once the database is running, the back-end application can be run locally pointing at Cognito in
the development AWS account by setting up environment variables to point at the Cognito user pool
by running the following:

```bash
export COGNITO_USER_POOL_ID={user-pool-id}
export COGNITO_APP_CLIENT_ID={app-client-id}
```

Then providing the local spring profile from the command line by running, the command below.

Note - you will need to configure the stubbed cognito import to generate a user using
your cognito sub from DEV, you can get this value by inspecting one of your cognito tokens in
from the DEV environment, or by logging into the AWS console to check the subject against the user credentials
you would like to use to log in locally. Once you have this value you can set it either as
an environment variable named `GS_ADMIN_SUBS={YOUR_SUBS}` or by setting a command line property
`--stubbed.users.gs-admin.subs={YOUR_SUBS}` as in the example below:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local '--stubbed.users.gs-admin.subs={YOUR_SUBS}'"
```

Note - in both cases `YOUR_SUBS` can be a single value or pipe separated list of UUID values, and it is possible
to set other role subjects as well as gs-admin (e.g. `stubbed.users.project-developers.subs` or
`PROJECT_DEVELOPER_SUBS` for project developers) the full list of variables for each role are listed below:

* `GS-Admin` -> `GS_ADMIN_SUBS` or `stubbed.users.gs-admin.subs`
* `Project-Developer` -> `PROJECT_DEVELOPER_SUBS` or `stubbed.users.project-developers.subs`
* `Arm-team` -> `ARM_TEAM_SUBS` or `stubbed.users.arm-team.subs`
* `GS-Registry` -> `GS_REGISTRY_SUBS` or `stubbed.users.gs-registry.subs`
* `GS-Finance` -> `GS_FINANCE_SUBS` or `stubbed.users.gs-finance.subs`
* `GS-Reviewer` -> `GS_REVIEWER_SUBS` or `stubbed.users.gs-reviewer.subs`
* `VVB` -> `VVB_SUBS` or `stubbed.users.vvb.subs`
* `TAC` -> `TAC_SUBS` or `stubbed.users.tac.subs`

With the API up and running pointing at DEV cognito you will be able to run the
[front-end](https://git.iig.caci.co.uk/gold-standard/front-end) pointing at the `back-end` instance running on your
local machine.

## Testing MFA in DEV Cognito
If you want to test MFA in DEV Cognito, you will need to set the a valid IdpUserId in dev aws to one of the 
stubbed users in the database, you can then set mfa up for this account as an administrator or once logged in


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

## Without DEV Cognito to run API standalone

The same command can be used to run the test profile for the backend application, although
using this method will not work with the `front-end` running locally.

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=test"
```

You can then get a token using the /token endpoint and pass this in the auth header as a bearer token for all requests.

Example curl command for getting a locally generated Admin token:<br>
```TOKEN=$(curl -s "http://127.0.0.1:8080/api/token?role=GS-Admin")```

Example curl command for getting the projects list using the above generated token:<br>
```curl -v POST http://127.0.0.1:8080/api/projects -H 'Authorization: Bearer '$TOKEN'' | json_pp```

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

