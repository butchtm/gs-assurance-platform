# tfsec:ignore:aws-ec2-require-vpc-flow-logs-for-all-vpcs
resource "aws_vpc" "gs_vpc" {
  #checkov:skip=CKV2_AWS_11:"Add logging later"
  cidr_block = "10.42.0.0/20"
  # This gives us an AWS IPV6 CIDR block with a /56 prefix length
  assign_generated_ipv6_cidr_block = true
  enable_dns_hostnames             = true
  enable_dns_support               = true
  tags = {
    Name = "${var.app_name}-vpc"
  }
}

# Routing tables to route traffic for backend Subnet
resource "aws_route_table" "backend_route_table" {
  vpc_id = aws_vpc.gs_vpc.id

  tags = {
    Name = "${var.environment}-backend-route-table"
  }
}

# Routing tables to route traffic for database Subnet
resource "aws_route_table" "database_route_table" {
  vpc_id = aws_vpc.gs_vpc.id

  tags = {
    Name = "${var.environment}-database-route-table"
  }
}

# Routing tables to route traffic for Public Subnet
resource "aws_route_table" "frontend_route_table" {
  vpc_id = aws_vpc.gs_vpc.id

  tags = {
    Name = "${var.environment}-frontend-route-table"
  }
}

# Routing tables for NAT public subnet
resource "aws_route_table" "nat_route_table" {
  vpc_id = aws_vpc.gs_vpc.id

  tags = {
    Name = "${var.environment}-nat-route-table"
  }
}

# Route table for the Bastion subnet
resource "aws_route_table" "bastion_route_table" {
  vpc_id = aws_vpc.gs_vpc.id

  tags = {
    Name = "${var.environment}-bastion-route-table"
  }
}

# Route for Internet Gateway
resource "aws_route" "public_internet_gateway" {
  route_table_id         = aws_route_table.frontend_route_table.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.gs_ig.id
}

# Route for Internet Gateway
resource "aws_route" "public_internet_gateway_ipv6" {
  route_table_id              = aws_route_table.frontend_route_table.id
  destination_ipv6_cidr_block = "::/0"
  gateway_id                  = aws_internet_gateway.gs_ig.id
}

# NAT Route Table Route for Internet Gateway
resource "aws_route" "nat_internet_gateway" {
  route_table_id         = aws_route_table.nat_route_table.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.gs_ig.id
}


# Route for NAT
resource "aws_route" "backend_nat_gateway" {
  route_table_id         = aws_route_table.backend_route_table.id
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id         = aws_nat_gateway.backend_nat.id
}

# AWS / Azure route for default route table
resource "aws_route" "aws_azure_route" {
  for_each = toset(var.azure_cidr_blocks)

  route_table_id         = aws_vpc.gs_vpc.default_route_table_id
  destination_cidr_block = each.value
  gateway_id             = aws_vpn_gateway.aws_azure_vpn_gateway.id
}

# AWS / Azure route for database route table
resource "aws_route" "aws_azure_db_route" {
  for_each = toset(var.azure_cidr_blocks)

  route_table_id         = aws_route_table.database_route_table.id
  destination_cidr_block = each.value
  gateway_id             = aws_vpn_gateway.aws_azure_vpn_gateway.id
}

# Route for Bastion route table
resource "aws_route" "bastion_internet_gateway" {
  route_table_id         = aws_route_table.bastion_route_table.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.gs_ig.id
}

# Route table associations for both Public & Private Subnets
resource "aws_route_table_association" "public_rt_assoc" {
  subnet_id      = aws_subnet.gs_subnet_public.id
  route_table_id = aws_route_table.frontend_route_table.id
}

resource "aws_route_table_association" "public_2_rt_assoc" {
  subnet_id      = aws_subnet.gs_subnet_public_2.id
  route_table_id = aws_route_table.frontend_route_table.id
}

resource "aws_route_table_association" "private_rt_assoc" {
  subnet_id      = aws_subnet.gs_subnet_private.id
  route_table_id = aws_route_table.backend_route_table.id
}

resource "aws_route_table_association" "database_rt_assoc" {
  subnet_id      = aws_subnet.gs_subnet_database.id
  route_table_id = aws_route_table.database_route_table.id
}

resource "aws_route_table_association" "database_rt_assoc_2" {
  subnet_id      = aws_subnet.gs_subnet_database_2.id
  route_table_id = aws_route_table.database_route_table.id
}

resource "aws_route_table_association" "nat_rt_assoc" {
  subnet_id      = aws_subnet.gs_subnet_public_nat.id
  route_table_id = aws_route_table.nat_route_table.id
}

resource "aws_route_table_association" "bastion_rt_assoc" {
  subnet_id      = aws_subnet.gs_subnet_public_bastion.id
  route_table_id = aws_route_table.bastion_route_table.id
}

# Default Security Group of VPC
# tfsec:ignore:aws-vpc-add-description-to-security-group
resource "aws_default_security_group" "gs_default_vpc_sg" {
  vpc_id = aws_vpc.gs_vpc.id
  depends_on = [
    aws_vpc.gs_vpc
  ]
}

resource "aws_vpc_endpoint" "s3_vpc_endpoint" {
  vpc_id       = aws_vpc.gs_vpc.id
  service_name = "com.amazonaws.eu-central-1.s3"
}

resource "aws_vpc_endpoint_policy" "s3_vpce_policy" {
  vpc_endpoint_id = aws_vpc_endpoint.s3_vpc_endpoint.id
  policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Sid" : "ECR access for ECS",
        "Effect" : "Allow",
        "Principal" : "*",
        "Action" : [
          "s3:GetObject"
        ],
        "Resource" : [
          "arn:aws:s3:::prod-eu-central-1-starport-layer-bucket/*"
        ]
      },
      {
        "Sid" : "Document bucket access - list",
        "Effect" : "Allow",
        "Principal" : "*",
        "Action" : [
          "s3:ListBucket"
        ],
        "Resource" : [
          "arn:aws:s3:::gs-ssrnc-dcmts-${var.environment}"
        ]
      },
      {
        "Sid" : "Document bucket objects - full access",
        "Effect" : "Allow",
        "Principal" : "*",
        "Action" : [
          "s3:*Object*"
        ],
        "Resource" : [
          "arn:aws:s3:::gs-ssrnc-dcmts-${var.environment}/*"
        ]
      },
      {
        "Sid" : "Download bucket objects - full access",
        "Effect" : "Allow",
        "Principal" : "*",
        "Action" : [
          "s3:*Object*"
        ],
        "Resource" : [
          "arn:aws:s3:::gs-ssrnc-dwlds-${var.environment}/*"
        ]
      },
      {
        "Sid" : "Scanning bucket objects - full access",
        "Effect" : "Allow",
        "Principal" : "*",
        "Action" : [
          "s3:GetObject",
          "s3:ListBucket",
          "s3:PutObject"
        ],
        "Effect" : "Allow"
        "Resource" : [
          "arn:aws:s3:::gs-ssrnc-scan-${var.environment}",
          "arn:aws:s3:::gs-ssrnc-scan-${var.environment}/*"
        ]
      }
    ]
  })
}

resource "aws_vpc_endpoint_route_table_association" "gs_vpc_endpoint_route" {
  route_table_id  = aws_route_table.backend_route_table.id
  vpc_endpoint_id = aws_vpc_endpoint.s3_vpc_endpoint.id
}
