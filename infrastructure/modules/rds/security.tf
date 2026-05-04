# tfsec:ignore:aws-ec2-no-public-egress-sgr
resource "aws_security_group" "rds_sq" {
  name        = "assurance-db-sg-${var.environment}"
  description = "security group to control access to rds instance"
  vpc_id      = var.main_vpc_id

  ingress {
    description     = "assurance db access"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [var.backend_sg_id]
  }

  ingress {
    description     = "bastion access"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.bastion.id]
  }

  ingress {
    description = "data project access"
    from_port   = 5432
    protocol    = "tcp"
    to_port     = 5432
    cidr_blocks = var.azure_cidr_blocks
  }

  egress {
    description = "allow all output"
    from_port   = 0
    to_port     = 0
    protocol    = -1
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "bastion" {
  #checkov:skip=CKV2_AWS_5: "Security group is attached through variables in other module"
  name        = "bastion-${var.environment}"
  description = "Allow SSH tunnelling into the RDS database"
  vpc_id      = var.main_vpc_id
}


# Key format for the bastion allow_list is {cidr}|{port}
resource "aws_vpc_security_group_ingress_rule" "bastion_inbound" {
  security_group_id = aws_security_group.bastion.id
  for_each          = var.bastion_allowlist.ipv4
  description       = "Enable incoming SSH: ${each.value}"
  from_port         = parseint(split("|", each.key)[1], 10)
  to_port           = parseint(split("|", each.key)[1], 10)
  ip_protocol       = "tcp"
  cidr_ipv4         = split("|", each.key)[0]
  tags              = { "Name" : "${each.value}: SSH" }
}


resource "aws_vpc_security_group_egress_rule" "bastion_outbound_tunnel" {
  security_group_id            = aws_security_group.bastion.id
  description                  = "Enable SSH tunnelling to RDS database"
  from_port                    = 5432
  to_port                      = 5432
  ip_protocol                  = "tcp"
  referenced_security_group_id = aws_security_group.rds_sq.id
}

# tfsec:ignore:aws-ec2-no-public-egress-sgr
resource "aws_vpc_security_group_egress_rule" "bastion_outbound_aws" {
  security_group_id = aws_security_group.bastion.id
  description       = "Enable access to AWS resources"
  ip_protocol       = "tcp"
  from_port         = 443
  to_port           = 443
  cidr_ipv4         = "0.0.0.0/0"
}
