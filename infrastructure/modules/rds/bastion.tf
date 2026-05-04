# Bastion instance user data

data "template_cloudinit_config" "rds_bastion_config" {
  gzip          = true
  base64_encode = true

  part {
    content_type = "text/cloud-config"
    content = templatefile("${path.module}/userdata/cloud-config.yaml", {
      cw_log_group = aws_cloudwatch_log_group.rds_bastion_log_group.id
    })
  }
}

# Bastion instance IAM

resource "aws_iam_role" "rds_bastion_role" {
  name_prefix        = "rds-bastion-instance-role-${var.environment}"
  assume_role_policy = data.aws_iam_policy_document.rds_bastion.json
}

data "aws_iam_policy_document" "rds_bastion" {
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

resource "aws_iam_role_policy_attachment" "rds_bastion_policy_cw" {
  role       = aws_iam_role.rds_bastion_role.name
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy"
}

resource "aws_iam_role_policy_attachment" "rds_bastion_policy_ssm" {
  role       = aws_iam_role.rds_bastion_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2RoleforSSM"
}

resource "aws_iam_instance_profile" "rds_bastion_instance_profile" {
  name = "rds-bastion-instance-profile-${var.environment}"
  role = aws_iam_role.rds_bastion_role.name
}

# Bastion instance

data "aws_ami" "rds_bastion_ami" {
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

resource "aws_eip" "rds_public_ip" {
  instance = aws_instance.rds_bastion.id
  domain   = "vpc"

  tags = {
    Name = "rds-bastion-ip-${var.environment}"
  }
}

# tfsec:ignore:aws-ec2-enable-at-rest-encryption
# tfsec:ignore:aws-ec2-enforce-http-token-imds
resource "aws_instance" "rds_bastion" {
  #checkov:skip=CKV_AWS_126: "CloudWatch Agent monitoring is installed using cloud-init about"
  #checkov:skip=CKV_AWS_135: "EBS not necessary in this instance"
  #checkov:skip=CKV_AWS_88: "Bastion needs public ip to be accessed"
  #checkov:skip=CKV_AWS_79: "Ensure Instance Metadata Service Version 1 is not enabled"
  #checkov:skip=CKV_AWS_8: "No launch config or ECS so not need for encryption"
  ami                  = data.aws_ami.rds_bastion_ami.id
  instance_type        = "t4g.nano"
  iam_instance_profile = aws_iam_instance_profile.rds_bastion_instance_profile.name
  vpc_security_group_ids = [
    aws_security_group.bastion.id
  ]

  subnet_id                   = var.bastion_subnet_id
  associate_public_ip_address = true

  user_data_base64 = data.template_cloudinit_config.rds_bastion_config.rendered

  tags = {
    Name = "rds-bastion-${var.environment}"
  }
}

# Bastion instance logging

# tfsec:ignore:aws-cloudwatch-log-group-customer-key
resource "aws_cloudwatch_log_group" "rds_bastion_log_group" {
  #checkov:skip=CKV_AWS_158: "Ensure that CloudWatch Log Group is encrypted by KMS"
  name              = "rds-bastion-log-group-${var.environment}"
  retention_in_days = 365

  tags = {
    Name = "rds-bastion-log-group-${var.environment}"
  }
}