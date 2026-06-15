app_name                             = "gold-standard"
environment                          = "staging"
region                               = "eu-central-1"
rds_connection_string                = "jdbc:postgresql://gs-assurance-db.cxwel2dox3sm.eu-central-1.rds.amazonaws.com:5432/assurance_db?currentSchema=gs"
rds_master_pw_secrets_name           = "staging/assurance_db_master_new"
rds_backend_pw_secrets_name          = "staging/assurance_db_backend_new"
frontend_image_tag                   = "1.22.1"
backend_image_tag                    = "1.22.1"
encryption_certificate_name          = "staging.goldstandard-assurance.org"
rds_kms_key_id                       = "8af748db-baa8-45b5-b61e-abbf9c8fc2f5"
iam_user                             = "gitlab-cicd-caci"
notification_contact_mapping_enabled = true
notification_from_email              = "noreply@goldstandard.org"
enable_application_ip_restriction    = true
registry_project_api_host            = "https://staging-gs-registry-export-sustaincert.onrender.com"
registry_project_api_link_template   = "https://staging.goldstandard-assurance.org/project-documents/%s"
registry_ui_host                     = "https://staging-registry.goldstandard.org"
sdg_tool_project_api_host            = "https://goldstandard-sdg-tool-staging.web.app"
web_ui_hostname                      = "https://staging.goldstandard-assurance.org"
document_pii_scanning_enabled        = false
enable_document_migration_instance   = false
azure_ip_address                     = "4.226.29.148"
azure_cidr_blocks                    = ["10.100.0.0/22", "10.100.4.0/22"]

acm_certificates = {
  "staging" : {
    "domain_name" : "staging.goldstandard-assurance.org",
    "id" : "arn:aws:acm:eu-central-1:874180492388:certificate/0018959c-0ca2-407a-bf35-736d36a26661"
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

monitoring_notifications_list = {
  "scronk" : "scronk@caci.co.uk",
  "mruocco" : "mruocco@caci.co.uk",
  "rburnett" : "rburnett@caci.co.uk",
  "teams_channel" : "8da2a68b.caciuk.onmicrosoft.com@emea.teams.ms"
}

backend_task_sizing = {
  cpu        = 2048
  memory     = 4096
  containers = 3
}

frontend_task_sizing = {
  cpu        = 512
  memory     = 1024
  containers = 3
}

rds_backup_retention_period     = 14
rds_multi_az                    = false
rds_instance_class              = "db.t4g.medium"
rds_create_read_replica         = false
rds_read_replica_instance_class = "db.t4g.small"
rds_monitoring_interval         = 0
