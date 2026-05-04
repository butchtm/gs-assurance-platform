# SSH Access is restricted to trusted IP addresses
resource "aws_security_group" "document_migration_sg" {
  name        = "doc-migration-${var.environment}"
  description = "Security group for document migration instance"
  vpc_id      = var.aws_vpc

  # SSH Ingress - CACI Kensington Office
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["46.149.111.41/32", "149.107.71.41/32"]
    description = "Enable incoming SSH: CACI VPN"
  }

  # HTTPS Ingress - CACI GitLab Runners
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["46.149.111.0/24", "18.168.176.0/24"]
    description = "Enable incoming SSH: CACI gitlab runners"
  }

  #HTTPS Egress
  # tfsec:ignore:aws-ec2-no-public-egress-sgr
  egress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow outbound HTTPS traffic"
  }

  tags = {
    Name = "document-migration-sg-${var.environment}"
  }
}
