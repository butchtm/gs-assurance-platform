# Internet Gateway for Public Subnet
resource "aws_internet_gateway" "gs_ig" {
  vpc_id = aws_vpc.gs_vpc.id
  tags = {
    Name        = "${var.environment}-igw"
    Environment = var.environment
  }
}

# Elastic-IP (eip) for NAT
resource "aws_eip" "gs_nat_eip" {
  domain     = "vpc"
  depends_on = [aws_internet_gateway.gs_ig]
}

# NAT
resource "aws_nat_gateway" "backend_nat" {
  allocation_id = aws_eip.gs_nat_eip.id
  subnet_id     = aws_subnet.gs_subnet_public_nat.id

  tags = {
    Name = "backend-nat"
  }
}