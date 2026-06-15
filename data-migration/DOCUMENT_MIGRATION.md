The Assurance Platform project documents from the SC platform are stored in Azure Blob Storage.
A small Amazon Linux AWS EC2 instance with [Rclone](https://rclone.org/) is used to migrate the documents
into an S3 bucket.

# Azure Blob access

A read-only SAS token, IP-restricted to the EC2 migration host, is required. At the time of writing Sarah Cole
(sarah.cole@goldstandard.org) from Gold Standard and myself (mruocco@caci.co.uk) are able to issue a read-only,
IP-restricted SAS token for the `storscdata` storage account which contains the `scfilestore` blob container.
In order to issue the SAS token the IP address of the EC2 instance used run the rclone process that is reading
the data from Azure must be provided. You should be able to find this by looking at the EC2 instances in the
AWS console.

# EC2 instance setup

The document migration EC2 instances are managed by Terraform. They are currently not deployed as the migrations have been completed but if they need to be
recreated for any reason this can be done with the `enable_document_migration_instance` variable. In order to perform a document migration you will
need to add your ssh public key in the ssh config file for the EC2 instance which can be found
(here)[https://git.iig.caci.co.uk/gold-standard/infrastructure/-/blob/develop/modules/document_migration/userdata/cloud-config.yaml#L11]

Once you have updated the config, you will need to go into the AWS console and delete the existing document migration
EC2 instance, then once you deploy the infrastructure changes with your ssh key added a new EC2 instance will be
created that will include your ssh key.

**Note** - when looking for the EC2 instances you will need to ensure that your AWS console
is viewing the Europe/Frankfurt region, using the drop-down in the top right corner of the screen,
if it is your first time using the console it will likely default to US East/N. Virginia.

# Performing document migration

## Emptying the documents bucket

If you are running a migration into an existing environment that already contains data
and documents, then before running the migration the existing contents of the documents
bucket needs to be cleared. You can do this in the AWS console by selecting the S3 service
and then selecting the documents bucket which has the naming convention `gs-ssrnc-dcmts-{env}`
and then selecting clicking the empty button and following the instructions presented.

## Running the migration

Once the document migration EC2 instance is running, you will need to ssh onto it, you can do this by specifying
your private key and the hostname of the EC2 instance, an example of the command required is:

```bash
ssh -i "id_rsa" ec2-user@ec2-3-79-73-43.eu-central-1.compute.amazonaws.com
```

Where `id_rsa` is the name of the ssh key you configured, in order to get the hostname you can look up the
EC2 instance in the AWS console.

Once you are connected to a terminal prompt on the EC2 instance, you will need to update the
rclone config file to update the SAS URL used to access the azure storage blob (which you will
need to request from either mruocco@caci.co.uk or sarah.cole@goldstandard.org) once you have the
updated SAS URL, you can run the following command to update the config:

```sh
nano ~/.config/rclone/rclone.conf
```

The file will contain something like the following:

```sh
type = azureblob
account = AZStorageAccount
sas_url = {sas_url_here}

[gs_aws_target]
type = s3
provider = AWS
env_auth = true
region = eu-central-1
```

There may be an old `sas_url` configured, if so you will just need to replace the old value with
the updated value, once this is configured, you should be able to run the command below to kick off
the document migration. **Note** the process typically takes between 2 and 4 hours to complete

DEV:

```sh
nohup rclone -v copy gs_sc_azure_source:scfilestore/GSProjectDocuments gs_aws_target:gs-ssrnc-dcmts-development > rclone.out &
```

STAGING:

```sh
nohup rclone -v copy gs_sc_azure_source:scfilestore/GSProjectDocuments gs_aws_target:gs-ssrnc-dcmts-staging > rclone.out &
```

## Tracking the migration progress

This run the process in the background and writes the output to a file named rclone.out,
the reason for this is the ssh connection to the EC2 instance can drop, running the command
in this way means the rclone process will continue to run, you can track the progress
by checking the output being written to the rclone.out file by running:

```sh
tail -f --lines=100 rclone.out
```

Which will follow the log output and show the most recent 100 lines of files that have been migrated.
