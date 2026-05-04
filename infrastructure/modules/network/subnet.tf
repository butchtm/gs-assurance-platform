# tfsec:ignore:aws-ec2-no-public-ip-subnet
resource "aws_subnet" "gs_subnet_public" {
  #checkov:skip=CKV_AWS_130:"Restrict IP assignment later - IMPORTANT!!!!"
  vpc_id                  = aws_vpc.gs_vpc.id
  cidr_block              = "10.42.0.0/23"
  availability_zone       = "eu-central-1a"
  map_public_ip_on_launch = true
  # AWS requires the subnet to have a /64 prefix length, so newbits set to 8, as the AWS gives us a /56
  # 0 <= netnum < 2^newbits
  ipv6_cidr_block                 = cidrsubnet(aws_vpc.gs_vpc.ipv6_cidr_block, 8, 0)
  assign_ipv6_address_on_creation = true
  tags = {
    Name = "${var.app_name}-public-subnet"
  }
}

# tfsec:ignore:aws-ec2-no-public-ip-subnet
resource "aws_subnet" "gs_subnet_public_2" {
  #checkov:skip=CKV_AWS_130:"Restrict IP assignment later - IMPORTANT!!!!"
  vpc_id                  = aws_vpc.gs_vpc.id
  cidr_block              = "10.42.6.0/23"
  availability_zone       = "eu-central-1b"
  map_public_ip_on_launch = true
  # AWS requires the subnet to have a /64 prefix length, so newbits set to 8, as the AWS gives us a /56
  # 0 <= netnum < 2^newbits
  ipv6_cidr_block                 = cidrsubnet(aws_vpc.gs_vpc.ipv6_cidr_block, 8, 1)
  assign_ipv6_address_on_creation = true
  tags = {
    Name = "${var.app_name}-public-subnet-2"
  }
}

# tfsec:ignore:aws-ec2-no-public-ip-subnet
resource "aws_subnet" "gs_subnet_public_nat" {
  #checkov:skip=CKV_AWS_130:"Restrict IP assignment later - IMPORTANT!!!!"
  vpc_id                  = aws_vpc.gs_vpc.id
  cidr_block              = "10.42.10.0/23"
  availability_zone       = "eu-central-1b"
  map_public_ip_on_launch = true
  tags = {
    Name = "${var.app_name}-gs-subnet-public-nat"
  }
}

# tfsec:ignore:aws-ec2-no-public-ip-subnet
resource "aws_subnet" "gs_subnet_public_bastion" {
  #checkov:skip=CKV_AWS_130:"Restrict IP assignment later - IMPORTANT!!!!"
  vpc_id                  = aws_vpc.gs_vpc.id
  cidr_block              = "10.42.12.0/23"
  availability_zone       = "eu-central-1b"
  map_public_ip_on_launch = true
  tags = {
    Name = "${var.app_name}-gs-subnet-public-bastion"
  }
}

resource "aws_subnet" "gs_subnet_private" {
  vpc_id                  = aws_vpc.gs_vpc.id
  cidr_block              = "10.42.2.0/23"
  availability_zone       = "eu-central-1b"
  map_public_ip_on_launch = false

  tags = {
    Name = "${var.app_name}-private-subnet"
  }
}

resource "aws_subnet" "gs_subnet_database" {
  vpc_id                  = aws_vpc.gs_vpc.id
  cidr_block              = "10.42.4.0/23"
  availability_zone       = "eu-central-1c"
  map_public_ip_on_launch = false

  tags = {
    Name = "${var.app_name}-database-subnet"
  }
}

resource "aws_subnet" "gs_subnet_database_2" {
  vpc_id                  = aws_vpc.gs_vpc.id
  cidr_block              = "10.42.8.0/23"
  availability_zone       = "eu-central-1b"
  map_public_ip_on_launch = false

  tags = {
    Name = "${var.app_name}-database-subnet-2"
  }
}