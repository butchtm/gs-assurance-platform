variable "app_name" {
  type        = string
  default     = "gold-standard"
  description = "This is the gold standard application"
}

variable "environment" {
  type        = string
  default     = "development"
  description = "This is for defining the evironment"
}

variable "region" {
  type        = string
  default     = "eu-central-1"
  description = "region to deploy resouces in"
}

variable "rds_connection_string" {
  type        = string
  description = "connection string for rds, this should be removed when rds via TF is fully setup"
}

variable "rds_master_pw_secrets_name" {
  type        = string
  description = "name of the secret containing the db master password"
}

variable "rds_backend_pw_secrets_name" {
  type        = string
  description = "name of the secret containing the backend user db password"
}

variable "frontend_image_tag" {
  type        = string
  description = "ECR image tag for the front end ECS service"
}

variable "backend_image_tag" {
  type        = string
  description = "ECR image tag for the front end ECS service"
}

variable "encryption_certificate_name" {
  type        = string
  description = "certificate domain name"
}

variable "acm_certificates" {}

variable "route53_zone" {}

variable "route53_records" {}

variable "route53_alias_records" {}

variable "user_sync_check_cron" {
  type        = string
  description = "How often to check if a user sync should run"
  default     = "0 */5 * ? * *"
}

variable "user_sync_sync_interval" {
  type        = number
  description = "How often a user sync should be run (in seconds)"
  default     = 295
}

variable "notification_contact_mapping_enabled" {
  type        = bool
  description = "Whether contact mapping overrides are enabled for email notifications"
  default     = true
}

variable "notification_from_email" {
  type        = string
  description = "FROM address used for any email notification sent by the assurance platform"
}

variable "application_allowlist" {
  type = object({
    ipv4 = map(string)
    ipv6 = map(string)
  })
  description = "Assurance application IPV4 and IPV6 allowlists - each a map of descriptions for the permitted CIDR blocks"
}

variable "bastion_allowlist" {
  type = object({
    ipv4 = map(string)
    ipv6 = map(string)
  })
  description = "Assurance bastion IPV4 and IPV6 allowlists - each a map of descriptions for the permitted CIDR blocks"
}

variable "monitoring_notifications_list" {
  type        = map(string)
  description = "A list of the emails we need to notify when a CloudWatch alarm goes off"
}

variable "rds_kms_key_id" {
  type        = string
  description = "AWS managed KMS key ID for RDS snapshot encryption"
}

variable "iam_user" {
  type        = string
  description = "The name of the IAM user which allows GitLab to authenticate"
}

variable "backend_task_sizing" {
  type = object({
    cpu        = number
    memory     = number
    containers = number
  })
  description = "Back-end (ECS) task sizing"
  default = {
    cpu        = 512
    memory     = 1024
    containers = 1
  }
}

variable "frontend_task_sizing" {
  type = object({
    cpu        = number
    memory     = number
    containers = number
  })
  description = "front-end (ECS) task sizing"
  default = {
    cpu        = 256
    memory     = 512
    containers = 1
  }
}

variable "rds_backup_retention_period" {
  type        = number
  description = "Number of days to retain db backups"
}

variable "rds_multi_az" {
  type        = bool
  description = "Multi AZ RDS, or not"
}

variable "rds_instance_class" {
  type        = string
  description = "RDS Instance Class"
}

variable "rds_create_read_replica" {
  type        = bool
  description = "Flag to enable creation of RDS read replica"
}

variable "rds_read_replica_instance_class" {
  type        = string
  description = "RDS Read Replica Instance Class"
}

variable "rds_monitoring_interval" {
  type        = number
  description = "RDS Enhanced Monitoring interval in seconds (0 => off, 1, 5, 10, 15, 30, 60)"
}

variable "enable_application_ip_restriction" {
  type        = bool
  description = "Flag to enable restricting access to the application to an IP whitelist"
}

variable "registry_project_api_host" {
  type        = string
  description = "Host name for registry project api"
}

variable "registry_project_api_token" {
  type        = string
  description = "Token used to access the registry project api"
}

variable "sdg_tool_project_api_host" {
  type        = string
  default     = ""
  description = "Host name for SDG tool project api"
}

variable "sdg_tool_project_api_token" {
  type        = string
  default     = ""
  description = "Token used to access the SDG tool project api"
}

variable "registry_project_api_link_template" {
  type        = string
  description = "Template hyperlink to be passed on registry api to link back to a project in assurance platform"
}

variable "registry_ui_host" {
  type        = string
  description = "Registry UI hostname used to build hyperlink to view project in registry"
}

variable "azure_storage_finance_connection_string" {
  type        = string
  default     = ""
  description = "Connection string used to access azure storage for the finance D365 integration"
}

variable "web_ui_hostname" {
  type        = string
  default     = ""
  description = "Hostname for the assurance platform front end UI from which the back-end is called"
}

variable "document_pii_scanning_enabled" {
  type        = bool
  description = "Flag to enable document PII scanning"
}

variable "enable_document_migration_instance" {
  type        = bool
  description = "Flag to enable the EC2 instance for document migration"
}

variable "azure_ip_address" {
  type        = string
  description = "IP address of the GS azure VPN"
}

variable "azure_cidr_blocks" {
  type        = set(string)
  description = "CIDR blocks of the GS azure VPN"
}