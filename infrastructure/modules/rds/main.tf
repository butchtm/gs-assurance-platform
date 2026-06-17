data "aws_secretsmanager_secret" "backend_db_pwd" { name = var.rds_master_pw_secrets_name }

data "aws_secretsmanager_secret_version" "current" {
  secret_id = data.aws_secretsmanager_secret.backend_db_pwd.id
}

resource "aws_db_subnet_group" "gs_db_subnet_group" {
  name       = "gs-db-subnet-group"
  subnet_ids = [var.db_subnet_id_1, var.db_subnet_id_2]

  tags = {
    Name = "gs-db-subnet-group"
  }
}

# tfsec:ignore:aws-rds-enable-performance-insights
resource "aws_db_instance" "db_gs_migration" {
  #checkov:skip=CKV_AWS_354:"not sure if we want encryption"
  #checkov:skip=CKV_AWS_353:"performance insights does have a cost if you move from free teir"
  #checkov:skip=CKV2_AWS_30:"add query logging if required"
  #checkov:skip=CKV_AWS_129:"check logging"
  #checkov:skip=CKV_AWS_226:"we do not want auto version bumps"
  #checkov:skip=CKV_AWS_118:"Enhanced monitoring not necessary"
  #checkov:skip=CKV_AWS_157:"Multi-AZ not necessary for every environment"
  allocated_storage    = 10
  db_name              = "assuranceDB"
  engine               = "postgres"
  engine_version       = "15.17"
  instance_class       = var.rds_instance_class
  username             = "assurance_dbadmin"
  password             = data.aws_secretsmanager_secret_version.current.secret_string
  identifier           = "gs-assurance-db"
  parameter_group_name = "default.postgres15"
  skip_final_snapshot  = true
  db_subnet_group_name = aws_db_subnet_group.gs_db_subnet_group.name
  apply_immediately    = true
  ca_cert_identifier   = "rds-ca-rsa2048-g1"
  #enabled_cloudwatch_logs_exports = ["error"]
  auto_minor_version_upgrade          = false
  storage_encrypted                   = true
  monitoring_interval                 = var.rds_monitoring_interval
  monitoring_role_arn                 = var.rds_monitoring_interval > 0 ? aws_iam_role.rds_enhanced_monitoring.arn : null
  iam_database_authentication_enabled = true
  deletion_protection                 = true
  multi_az                            = var.rds_multi_az
  copy_tags_to_snapshot               = true
  backup_retention_period             = var.rds_backup_retention_period
  vpc_security_group_ids              = [aws_security_group.rds_sq.id]
}

#tfsec:ignore:aws-rds-enable-performance-insights
resource "aws_db_instance" "db_gs_migration_replica" {
  #checkov:skip=CKV_AWS_354:"not sure if we want encryption"
  #checkov:skip=CKV_AWS_353:"performance insights does have a cost if you move from free teir"
  #checkov:skip=CKV2_AWS_30:"add query logging if required"
  #checkov:skip=CKV_AWS_129:"check logging"
  #checkov:skip=CKV_AWS_226:"we do not want auto version bumps"
  #checkov:skip=CKV_AWS_118:"Enhanced monitoring not necessary"
  #checkov:skip=CKV_AWS_157:"Multi-AZ not necessary for every environment"
  count               = var.rds_create_read_replica ? 1 : 0
  identifier          = "gs-assurance-db-read-replica"
  replicate_source_db = aws_db_instance.db_gs_migration.arn

  instance_class         = var.rds_read_replica_instance_class
  db_subnet_group_name   = aws_db_subnet_group.gs_db_subnet_group.name
  vpc_security_group_ids = [aws_security_group.rds_sq.id]

  storage_encrypted     = true
  deletion_protection   = true
  skip_final_snapshot   = true
  apply_immediately     = true
  copy_tags_to_snapshot = true
}
