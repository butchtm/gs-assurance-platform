resource "aws_customer_gateway" "aws_azure_customer_gateway" {
  bgp_asn    = 65000
  ip_address = var.azure_ip_address
  type       = "ipsec.1"

  tags = {
    Name = "cg-aws-azure-assurance-platfor"
  }
}

resource "aws_vpn_gateway" "aws_azure_vpn_gateway" {
  vpc_id = aws_vpc.gs_vpc.id

  tags = {
    Name = "vpg-aws-azure-assurance-platform"
  }
}


resource "aws_vpn_connection" "aws_azure_vpn_connection" {
  vpn_gateway_id      = aws_vpn_gateway.aws_azure_vpn_gateway.id
  customer_gateway_id = aws_customer_gateway.aws_azure_customer_gateway.id
  type                = "ipsec.1"
  static_routes_only  = true

  tags = {
    Name = "vpn-aws-azure-assurance-platform"
  }
}

resource "aws_vpn_connection_route" "aws_azure_vpn_connection_route" {
  for_each = toset(var.azure_cidr_blocks)

  destination_cidr_block = each.value
  vpn_connection_id      = aws_vpn_connection.aws_azure_vpn_connection.id
}