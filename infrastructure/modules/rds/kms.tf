provider "aws" {
  region = "eu-north-1"
  alias  = "replica"
}

resource "aws_kms_key" "db_gs_migration_key" {
  #checkov:skip=CKV_AWS_33: "Allow wildcards"
  description              = "Encryption key for automated backups"
  enable_key_rotation      = true
  customer_master_key_spec = "SYMMETRIC_DEFAULT"
  provider                 = aws.replica
  policy = jsonencode({
    Id = "example"
    Statement = [
      {
        Sid    = "Enable CICI Permissions",
        Action = ["kms:*"],
        Effect = "Allow",
        Principal = {
          AWS = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:user/${var.iam_user}"
        },
        Resource = "*"
      },
      {
        Sid    = "Allow full access to root user",
        Effect = "Allow",
        Principal = {
          AWS = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"
        },
        Action   = ["kms:*"],
        Resource = "*"
      },
      {
        Sid = "Allow direct access to key metadata to the account",
        Action = [
          "kms:Describe*",
          "kms:Get*",
          "kms:List*",
          "kms:RevokeGrant"
        ],
        Effect = "Allow",
        Principal = {
          AWS = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"
        },
        Resource = "*",
      },
      {
        Sid = "Allow access through RDS for all principals in the account that are authorized to use RDS",
        Action = [
          "kms:Encrypt",
          "kms:Decrypt",
          "kms:ReEncrypt*",
          "kms:GenerateDataKey*",
          "kms:CreateGrant",
          "kms:ListGrants",
          "kms:DescribeKey"
        ],
        Effect = "Allow",
        Principal = {
          AWS = "*"
        },
        Resource = "*",
        Condition = {
          StringEquals = {
            "kms:CallerAccount" = "${data.aws_caller_identity.current.account_id}",
            "kms:ViaService"    = "rds.eu-central-1.amazonaws.com"
          }
        }
      }
    ],
    Version = "2012-10-17"
  })
}

data "aws_caller_identity" "current" {}

resource "aws_db_instance_automated_backups_replication" "db_gs_migration_backup_replication" {
  source_db_instance_arn = aws_db_instance.db_gs_migration.arn
  kms_key_id             = aws_kms_key.db_gs_migration_key.arn
  retention_period       = 31
  provider               = aws.replica
}
