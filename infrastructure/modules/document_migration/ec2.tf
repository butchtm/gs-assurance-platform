data "template_cloudinit_config" "ec2_doc_migration_config" {
  gzip          = true
  base64_encode = true

  part {
    content_type = "text/cloud-config"
    content = templatefile("${path.module}/userdata/cloud-config.yaml", {
      cw_log_group = aws_cloudwatch_log_group.ec2_doc_mig_log_group.id
    })
  }
}

# tfsec:ignore:aws-cloudwatch-log-group-customer-key
resource "aws_cloudwatch_log_group" "ec2_doc_mig_log_group" {
  #checkov:skip=CKV_AWS_158: "Ensure that CloudWatch Log Group is encrypted by KMS"
  name              = "ec2-doc-mig-log-group-${var.environment}"
  retention_in_days = 365

  tags = {
    Name = "ec2-doc-mig-log-group-${var.environment}"
  }
}

resource "aws_iam_role" "document_migration_role" {
  count              = var.enable_instance ? 1 : 0
  name_prefix        = "document-migration-role-${var.environment}"
  assume_role_policy = data.aws_iam_policy_document.assume_role_policy.json
}

data "aws_iam_policy_document" "assume_role_policy" {
  statement {
    actions = [
      "sts:AssumeRole",
    ]

    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

resource "aws_iam_role_policy" "document_migration_policy" {
  count  = var.enable_instance ? 1 : 0
  name   = "doc-migration-policy-${var.environment}"
  role   = aws_iam_role.document_migration_role[count.index].id
  policy = data.aws_iam_policy_document.s3_permissions.json
}

data "aws_iam_policy_document" "s3_permissions" {
  statement {
    effect = "Allow"

    actions = [
      "s3:GetObject",
      "s3:PutObject",
      "s3:ListBucket"
    ]

    resources = [
      "${var.private_document_bucket.arn}/*",
      "${var.private_document_bucket.arn}"
    ]
  }
}

resource "aws_iam_role_policy_attachment" "document_migration_policy_cw" {
  count      = var.enable_instance ? 1 : 0
  role       = aws_iam_role.document_migration_role[count.index].name
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy"
}

resource "aws_iam_role_policy_attachment" "document_migration_policy_ssm" {
  count      = var.enable_instance ? 1 : 0
  role       = aws_iam_role.document_migration_role[count.index].name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2RoleforSSM"
}

resource "aws_iam_instance_profile" "document_migration_instance_profile" {
  count = var.enable_instance ? 1 : 0
  name  = "document-migration-profile-${var.environment}"
  role  = aws_iam_role.document_migration_role[count.index].name
}

data "aws_ami" "document_migration_ami" {
  owners      = ["amazon"]
  most_recent = true
  filter {
    name   = "architecture"
    values = ["arm64"]
  }
  filter {
    name   = "name"
    values = ["al2023-ami-2023.10*"]
  }
}

resource "aws_eip" "document_migration_public_ip" {
  count    = var.enable_instance ? 1 : 0
  instance = aws_instance.document_migration[count.index].id
  domain   = "vpc"

  tags = {
    Name = "document-migration-ip-${var.environment}"
  }
}

# tfsec:ignore:aws-ec2-enable-at-rest-encryption
resource "aws_instance" "document_migration" {
  #checkov:skip=CKV_AWS_126: "Monitoring not needed"
  #checkov:skip=CKV_AWS_8: "No launch config or ECS so not need for encryption"
  #checkov:skip=CKV_AWS_88: "ec2 needs public ip to be accessed"
  count                = var.enable_instance ? 1 : 0
  ami                  = data.aws_ami.document_migration_ami.id
  instance_type        = "t4g.small"
  iam_instance_profile = aws_iam_instance_profile.document_migration_instance_profile[count.index].name
  vpc_security_group_ids = [
    aws_security_group.document_migration_sg.id
  ]

  subnet_id                   = var.bastion_subnet_id
  associate_public_ip_address = true

  ebs_optimized = true
  monitoring    = false

  user_data_base64 = data.template_cloudinit_config.ec2_doc_migration_config.rendered

  tags = {
    Name = "document-migration-${var.environment}"
  }

  metadata_options {
    http_tokens                 = "required"
    http_put_response_hop_limit = 2
    http_endpoint               = "enabled"
  }
}