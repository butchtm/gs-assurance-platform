variable "app_name" {

  type = string
}

variable "environment" {
  type = string
}

variable "private_subnet_id" {}
variable "public_subnet_id" {}
variable "public_subnet_id_2" {}
variable "aws_vpc" {}

variable "cognito_app_client_id" {}

variable "cognito_pool_id" {}
variable "rds_connection_string" {}
variable "region" {}
variable "rds_pw_secrets_name" {}
variable "encryption_certificate_name" {}

variable "user_sync_check_cron" {}
variable "user_sync_sync_interval" {}
variable "notification_contact_mapping_enabled" {}
variable "notification_from_email" {}

variable "application_allowlist" {}
variable "monitoring_notifications_list" {}

variable "backend_image_tag" {}
variable "frontend_image_tag" {}

variable "backend_task_sizing" {}
variable "frontend_task_sizing" {}
variable "enable_application_ip_restriction" {}

variable "registry_project_api_host" {}
variable "registry_project_api_token" {}
variable "registry_project_api_link_template" {}
variable "registry_ui_host" {}
variable "sdg_tool_project_api_host" {}
variable "sdg_tool_project_api_token" {}
variable "azure_storage_finance_connection_string" {}
variable "web_ui_hostname" {}
variable "document_pii_scanning_enabled" {}