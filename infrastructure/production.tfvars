app_name                             = "gold-standard"
environment                          = "production"
region                               = "eu-central-1"
rds_connection_string                = "jdbc:postgresql://gs-assurance-db.cmzijfmmr6bg.eu-central-1.rds.amazonaws.com:5432/assurance_db?currentSchema=gs"
rds_master_pw_secrets_name           = "production/assurance_db_master"
rds_backend_pw_secrets_name          = "production/assurance_db_backend"
frontend_image_tag                   = "1.21.3"
backend_image_tag                    = "1.21.2"
encryption_certificate_name          = "assurance-platform.goldstandard.org"
rds_kms_key_id                       = "1e40f3d0-365c-4b07-b337-36f1a2b4b06e"
iam_user                             = "gitlab-pipeline-prod"
notification_contact_mapping_enabled = false
notification_from_email              = "noreply@goldstandard.org"
enable_application_ip_restriction    = false
registry_project_api_host            = "https://sustaincert-api-registry.goldstandard.org"
registry_project_api_link_template   = "https://assurance-platform.goldstandard.org/project-documents/%s"
registry_ui_host                     = "https://registry.goldstandard.org"
sdg_tool_project_api_host            = "https://sdg-tool.goldstandard.org"
web_ui_hostname                      = "https://assurance-platform.goldstandard.org"
document_pii_scanning_enabled        = false
enable_document_migration_instance   = false
azure_ip_address                     = "4.226.29.148"
azure_cidr_blocks                    = ["10.100.3.0/27", "10.100.10.0/27"]

application_allowlist = {
  ipv4 : {
  }
  ipv6 : {
  }
}

acm_certificates = {
  "prod" : {
    "domain_name" : "assurance-platform.goldstandard.org",
    "id" : "arn:aws:acm:eu-central-1:424774273116:certificate/6fc1e5c1-8f3e-47a4-b9e4-a014eeb5450b"

  }
  "prod-interim" : {
    "domain_name" : "goldstandard-assurance.org",
    "id" : "arn:aws:acm:eu-central-1:424774273116:certificate/2a9b1eee-0b35-4dad-be53-a957ce0d2641"

  }
}

route53_zone = {
  "prod" : {
    "domain_name" : "goldstandard-assurance.org",
    "id" : "Z0578876234A4N2X44ARJ"
  }
}

route53_records = {
  "prod-NS" : {
    "record_name" : "goldstandard-assurance.org",
    "ttl" : "172800"
    "type" : "NS"
    "records" : ["ns-735.awsdns-27.net.", "ns-1255.awsdns-28.org.", "ns-424.awsdns-53.com.", "ns-1861.awsdns-40.co.uk."]
    "id" : "Z0578876234A4N2X44ARJ_goldstandard-assurance.org_NS"
  }
  "prod-SOA" : {
    "record_name" : "goldstandard-assurance.org",
    "ttl" : "900"
    "type" : "SOA"
    "records" : ["ns-735.awsdns-27.net. awsdns-hostmaster.amazon.com. 1 7200 900 1209600 86400"]
    "id" : "Z0578876234A4N2X44ARJ_goldstandard-assurance.org_SOA"
  }
  "staging-CNAME" : {
    "record_name" : "staging.goldstandard-assurance.org",
    "ttl" : "300"
    "type" : "CNAME"
    "records" : ["dualstack.fe-application-lb-staging-2005484087.eu-central-1.elb.amazonaws.com"]
    "id" : "Z0578876234A4N2X44ARJ_staging.goldstandard-assurance.org_CNAME"
  }
  "staging-CNAME-cert-validation" : {
    "record_name" : "_aca2b9c59c8baa865dcd12836b8352b2.staging.goldstandard-assurance.org",
    "ttl" : "300"
    "type" : "CNAME"
    "records" : ["_88e1634fe4f24fcc5559b623eead80a4.djqtsrsxkq.acm-validations.aws."]
    "id" : "Z0578876234A4N2X44ARJ__aca2b9c59c8baa865dcd12836b8352b2.staging.goldstandard-assurance.org_CNAME"
  }
  "dev-CNAME" : {
    "record_name" : "dev.goldstandard-assurance.org",
    "ttl" : "300"
    "type" : "CNAME"
    "records" : ["dualstack.fe-application-lb-development-425530418.eu-central-1.elb.amazonaws.com"]
    "id" : "Z0578876234A4N2X44ARJ_dev.goldstandard-assurance.org_CNAME"
  }
  "dev-CNAME-cert-validation" : {
    "record_name" : "_398b4a157a4bf69f9dce9f5d27798c10.dev.goldstandard-assurance.org",
    "ttl" : "300"
    "type" : "CNAME"
    "records" : ["_336e3569f8ba347503baad8c3ef40712.djqtsrsxkq.acm-validations.aws."]
    "id" : "Z0578876234A4N2X44ARJ__398b4a157a4bf69f9dce9f5d27798c10.dev.goldstandard-assurance.org_CNAME"
  }
  "prod-CNAME-cert-validation" : {
    "record_name" : "_2b8bff4570fa09cde1b8b06f1ed426c6.goldstandard-assurance.org",
    "ttl" : "300"
    "type" : "CNAME"
    "records" : ["_e2b9c99cc652895fb46e96f6d95d9571.mhbtsbpdnt.acm-validations.aws."]
    "id" : "Z0578876234A4N2X44ARJ__2b8bff4570fa09cde1b8b06f1ed426c6.goldstandard-assurance.org_CNAME"
  }
}

route53_alias_records = {
  "prod-A" : {
    "record_name" : "goldstandard-assurance.org",
    "alias_name" : "dualstack.fe-application-lb-production-2143658666.eu-central-1.elb.amazonaws.com"
    "alias_id" : "Z215JYRZR1TBD5"
    "id" : "Z0578876234A4N2X44ARJ_goldstandard-assurance.org_A"
  }
}

bastion_allowlist = {
  ipv4 : {
    "46.149.111.41/32|22" : "CACI primary VPN"
    "149.107.71.41/32|22" : "CACI secondary VPN"
    "46.149.111.0/24|443" : "CACI gitlab runners (old nexus)",
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

rds_backup_retention_period     = 31
rds_multi_az                    = true
rds_instance_class              = "db.t4g.medium"
rds_create_read_replica         = true
rds_read_replica_instance_class = "db.t4g.small"
rds_monitoring_interval         = 60
