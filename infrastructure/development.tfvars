app_name                             = "gold-standard"
environment                          = "development"
region                               = "eu-central-1"
rds_connection_string                = "jdbc:postgresql://gs-assurance-db.crl3qr7cxvje.eu-central-1.rds.amazonaws.com:5432/assurance_db?currentSchema=gs"
rds_master_pw_secrets_name           = "assurance_db_master_dev_new"
rds_backend_pw_secrets_name          = "assurance_db_backend_dev_new"
frontend_image_tag                   = "latest"
backend_image_tag                    = "latest"
encryption_certificate_name          = "dev.goldstandard-assurance.org"
rds_kms_key_id                       = "129334c4-6f10-44e1-90c8-9f66812b0381"
iam_user                             = "gitlab-pipeline-dev"
notification_contact_mapping_enabled = true
notification_from_email              = "noreply@goldstandard.org"
enable_application_ip_restriction    = true
registry_project_api_host            = "https://staging-gs-registry-export-sustaincert.onrender.com"
registry_project_api_link_template   = "https://dev.goldstandard-assurance.org/project-documents/%s"
registry_ui_host                     = "https://staging-registry.goldstandard.org"
sdg_tool_project_api_host            = "https://goldstandard-sdg-tool-staging.web.app"
web_ui_hostname                      = "https://dev.goldstandard-assurance.org"
document_pii_scanning_enabled        = false
enable_document_migration_instance   = false
azure_ip_address                     = "4.226.29.148"
azure_cidr_blocks                    = ["10.100.0.0/22", "10.100.4.0/22"]

acm_certificates = {
  "dev" : {
    "domain_name" : "dev.goldstandard-assurance.org",
    "id" : "arn:aws:acm:eu-central-1:929565130624:certificate/db38a1f7-1938-4ee5-b185-cbe441f94659"
  }
}

route53_zone = {}

route53_records = {}

route53_alias_records = {}

application_allowlist = {
  ipv4 : {
    "46.149.111.41/32" : "CACI primary VPN"
    "149.107.71.41/32" : "CACI secondary VPN"
    "46.149.111.0/24" : "CACI On Premise Gitlab Runner"
    "74.118.125.50/32" : "GS: VPN"
    "146.70.142.206/32" : "GS: VPN"
  }
  ipv6 : {
  }
}

bastion_allowlist = {
  ipv4 : {
    "46.149.111.41/32|22" : "CACI primary VPN"
    "149.107.71.41/32|22" : "CACI secondary VPN"
    "46.149.111.0/24|443" : "CACI gitlab runners (old nexus)"
    "18.168.176.0/24|22" : "New CACI nexus"
    "18.168.176.0/24|443" : "New CACI nexus"
  }
  # Reserved for future use: Bastion is not on a dualstack subnet
  ipv6 : {
  }
}

monitoring_notifications_list = {}

rds_backup_retention_period     = 7
rds_multi_az                    = false
rds_instance_class              = "db.t4g.small"
rds_create_read_replica         = false
rds_read_replica_instance_class = "db.t4g.micro"
rds_monitoring_interval         = 0
