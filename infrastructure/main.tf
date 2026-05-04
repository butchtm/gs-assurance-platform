module "cognito_user_pool" {
  source                  = "./modules/cognito_user_pool"
  environment             = var.environment
  user_pool_name          = "gs-assurance-user-pool"
  assurance_email_arn     = module.ses.assurance_email_arn
  monitoring_topic_arn    = module.ecs.monitoring_topic_arn
  application_url         = var.encryption_certificate_name
  notification_from_email = var.notification_from_email
}

module "document_migration" {
  source                  = "./modules/document_migration"
  environment             = var.environment
  aws_vpc                 = module.network.gs_vpc_id
  bastion_subnet_id       = module.network.bastion_subnet_id
  private_document_bucket = module.ecs.private_document_bucket
  enable_instance         = var.enable_document_migration_instance
}

module "ecs" {
  source                                  = "./modules/ecs"
  environment                             = var.environment
  app_name                                = var.app_name
  private_subnet_id                       = module.network.private_subnet_id
  public_subnet_id                        = module.network.public_subnet_id
  public_subnet_id_2                      = module.network.public_subnet_id_2
  aws_vpc                                 = module.network.gs_vpc_id
  cognito_pool_id                         = module.cognito_user_pool.cognito_user_pool_id
  cognito_app_client_id                   = module.cognito_user_pool.cognito_app_client_id
  rds_connection_string                   = var.rds_connection_string
  rds_pw_secrets_name                     = var.rds_backend_pw_secrets_name
  region                                  = var.region
  encryption_certificate_name             = var.encryption_certificate_name
  user_sync_check_cron                    = var.user_sync_check_cron
  user_sync_sync_interval                 = var.user_sync_sync_interval
  notification_contact_mapping_enabled    = var.notification_contact_mapping_enabled
  notification_from_email                 = var.notification_from_email
  application_allowlist                   = var.application_allowlist
  backend_image_tag                       = var.backend_image_tag
  frontend_image_tag                      = var.frontend_image_tag
  backend_task_sizing                     = var.backend_task_sizing
  frontend_task_sizing                    = var.frontend_task_sizing
  enable_application_ip_restriction       = var.enable_application_ip_restriction
  registry_project_api_host               = var.registry_project_api_host
  registry_project_api_token              = var.registry_project_api_token
  registry_project_api_link_template      = var.registry_project_api_link_template
  registry_ui_host                        = var.registry_ui_host
  monitoring_notifications_list           = var.monitoring_notifications_list
  sdg_tool_project_api_host               = var.sdg_tool_project_api_host
  sdg_tool_project_api_token              = var.sdg_tool_project_api_token
  azure_storage_finance_connection_string = var.azure_storage_finance_connection_string
  web_ui_hostname                         = var.web_ui_hostname
  document_pii_scanning_enabled           = var.document_pii_scanning_enabled
}

module "network" {
  source                = "./modules/network"
  environment           = var.environment
  app_name              = var.app_name
  acm_certificates      = var.acm_certificates
  route53_zone          = var.route53_zone
  route53_records       = var.route53_records
  route53_alias_records = var.route53_alias_records
  azure_ip_address      = var.azure_ip_address
  azure_cidr_blocks     = var.azure_cidr_blocks
}

module "rds" {
  source                          = "./modules/rds"
  db_subnet_id_1                  = module.network.database_subnet_id
  db_subnet_id_2                  = module.network.database_subnet_id_2
  backend_sg_id                   = module.ecs.backend_sg_id
  main_vpc_id                     = module.network.gs_vpc_id
  bastion_allowlist               = var.bastion_allowlist
  bastion_subnet_id               = module.network.bastion_subnet_id
  rds_master_pw_secrets_name      = var.rds_master_pw_secrets_name
  environment                     = var.environment
  rds_kms_key_id                  = var.rds_kms_key_id
  iam_user                        = var.iam_user
  rds_backup_retention_period     = var.rds_backup_retention_period
  rds_multi_az                    = var.rds_multi_az
  rds_instance_class              = var.rds_instance_class
  rds_create_read_replica         = var.rds_create_read_replica
  rds_read_replica_instance_class = var.rds_read_replica_instance_class
  rds_monitoring_interval         = var.rds_monitoring_interval
  monitoring_topic_arn            = module.ecs.monitoring_topic_arn
  azure_cidr_blocks               = var.azure_cidr_blocks
}

module "ses" {
  source                  = "./modules/ses"
  monitoring_topic_arn    = module.ecs.monitoring_topic_arn
  environment             = var.environment
  notification_from_email = var.notification_from_email
}
