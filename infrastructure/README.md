# Terraform Infrastructure

Terraform infrastructure for the Gold Standard Assurance Platform.

An architecture diagram is available - [architecture.drawio](./docs/architecture.drawio). It can be viewed using the free online tool [DrawIO](https://app.diagrams.net/).

## Quickstart

1. Install [Terraform](https://developer.hashicorp.com/terraform/install)
   (or manage your installations with
   [`tfenv`](https://github.com/tfutils/tfenv)).
1. Define your environment.
   During development, only use the `development` environment.
   1. Define the `GS_ENV_NAME` environment variable.
      For example you could run `export GS_ENV_NAME=development` in your terminal,
      or create a script with that set that you then `source`.
      There is [an example template script](./local.sh.template)
      you can use as a starting point if this is your preference.
      From the root of this repository, you can run:

      ```sh
      cp ./local.sh.template ./local.sh
      . ./local.sh
      ```
   1. Run `set_tf_env.sh`.
      This will delete any existing Terraform configuration,
      and generate a new copy of `providers.tf`
      from [the template](./providers.tf.template).
1. Prepare your AWS credentials.
   As for the environment, you could export your `AWS_ACCESS_KEY_ID`,
   `AWS_SECRET_ACCESS_KEY`, and `AWS_SESSION_TOKEN` in your terminal,
   or you could create a script with these set that you then `source`.
   Bear in mind that your credentials
   will require regular rotation to keep up to date.
1. Run `terraform init -reconfigure`.
   This will install the necessary Terraform providers,
   and initialise the Terraform modules.
1. Run `terraform validate` to check the scripts can be parsed successfully.
1. Run `terraform plan "-var-file=${GS_ENV_NAME}.tfvars"`.
   (If prompted to provide unspecified variables, for now,
   hit enter to proceed without values.)
   This will run a Terraform plan to compare the defined state
   against the current actual state of the infrastructure.
   Any proposed changes will be shown in the output,
   but nothing will be actioned from a `plan` operation.

## Modules
### Cognito
Contains all the application user management. Users are controlled by a Cognito User Pool.

### ECS
Contains the main application resources - ECS, ECR, Application Load Balancer, S3 document store.

### Network
Contains the VPC and associated resources - VPC, subnets, Internet Gateways and NACLs.

### RDS
Contains the RDS database and associated bastion instance.

##  Commands

### Format

Formatting via `terraform fmt -recursive` is run on commit by pre-commit. When the repository is cloned, ensure you have pre-commit [installed locally](https://pre-commit.com/#install) and run `pre-commit install`.

### Validate

`terraform validate` is run in a GitLab pipeline on
merge request to any of the `develop`, `staging` or `production` branches. It can also be run locally without AWS credentials.

### Plan

`terraform plan` is run in a GitLab pipeline on
merge request to any of the `develop`, `staging` or `production` branches. AWS credentials are necessary to run it locally.

### Apply

Once merged `terraform apply` is run on the relevant environment. For project consistency it should not be run locally - only from the GitLab pipeline.


## Initial Setup

### S3

When first setting up the AWS account for Terraform, the following 3 scripts need to be run. Replacing bucket-name, region and table-name with your respective values. This sets up the terraform state bucket and state locks.

`aws s3api create-bucket --bucket "gs-assurance-terraform-state-ENVIRONMENT" --region "region" --create-bucket-configuration LocationConstraint="region"`

`aws s3api put-bucket-versioning --bucket "gs-assurance-terraform-state-ENVIRONMENT" --versioning-configuration Status=Enabled`

`aws dynamodb create-table --table-name "gs-assurance-tf-state-lock-ENVIRONMENT" --attribute-definitions AttributeName=LockID,AttributeType=S --key-schema AttributeName=LockID,KeyType=HASH --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 --region "region"`

### IAM

An IAM user also needs to be created so that GitLab can access AWS, the credentials should be added as the following GitLab variables.
- `AWS_DEFAULT_REGION`
- `DEV_AWS_ACCESS_KEY_ID`
- `DEV_AWS_SECRET_ACCESS_KEY`
- `STAGING_AWS_ACCESS_KEY_ID`
- `STAGING_AWS_SECRET_ACCESS_KEY`
- `PROD_AWS_ACCESS_KEY_ID`
- `PROD_AWS_SECRET_ACCESS_KEY`

### IPs
Add any necessary IP addresses to [development.tf.vars](development.tfvars) and [staging.tf.vars](./staging.tfvars) as the development and staging applications are IP locked.

### Secrets

The RDS database needs both a master password and a password to allow the backend access, these need to be manually generated and given the following names:
- `ENVIRONMENT/assurance_db_master`
- `ENVIRONMENT/assurance_db_backend`

These have previously been generated using Password Safe.

### DNS
Domain names, certificates, hosted zones and other AWS Route53 resources are not configured within the terraform and have previously been manually created in the AWS console. The current DNS configuration within the Gold Standard AWS accounts is as follows. There is an interim production domain listed below which is used for testing before release.
<br/>
<br/>
The real production system will use http://assurance-platform.goldstandard.org/ however this is hosted by Gold Standard in CloudFlare and is not contained within our AWS environments. In order to connect the two we did the following:
- In AWS - created a certificate with the required domain name
- In CloudFlare - added a CNAME record from the chosen domain to the load balancer DNS name. Note this needs the `dualstack.` prefix in order for IPV4 and IPV6 to work.
- In CloudFlare - added a CNAME record as required to validate the certificate, the details will be available in AWS Certificate Manager
- In CloudFlare - added a CAA record of `amazon.com` in order to allow AWS to use a CloudFlare certificate, see the [documentation](https://docs.aws.amazon.com/acm/latest/userguide/troubleshooting-caa.html).

| Environment          | Hosted Zone                | Certificate                         |
| -------------------- | -------------------------- | ----------------------------------- |
| Development          | N/a (uses production zone) | dev.goldstandard-assurance.org      |
| Staging              | N/a (uses production zone) | staging.goldstandard-assurance.org  |
| Production (interim) | goldstandard-assurance.org | goldstandard-assurance.org          |
| Production (real)    | N/a (controlled by GS)     | assurance-platform.goldstandard.org |


### SES
Send a support email to AWS to get the environment out of sandbox mode with a lengthy email about how we will handle bounce backs and complaints as they require this.

Deploy the Terraform resources and then speak to Gold Standard to add the records present in the domain to their DNS, this should automatically verify the domain.