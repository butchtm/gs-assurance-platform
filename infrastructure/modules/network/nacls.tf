resource "aws_network_acl" "public_ipv6_subnets" {
  # checkov:skip=CKV2_AWS_1: "NACL is separately associated with subnets"
  vpc_id = aws_vpc.gs_vpc.id
  tags   = { Name = "NACL for Public dual stack subnets" }
}

# Allow any inbound port for ipv4 from the private subnet for the return connection back to the ALB
# tfsec:ignore:aws-ec2-no-excessive-port-access
resource "aws_network_acl_rule" "public_ipv6_subnet_all_from_private_subnet" {
  # checkov:skip=CKV_AWS_352: "Inbound from trusted subnet"
  network_acl_id = aws_network_acl.public_ipv6_subnets.id
  rule_number    = 90
  rule_action    = "allow"
  egress         = false
  protocol       = "all"
  cidr_block     = aws_subnet.gs_subnet_private.cidr_block
}

locals {
  inbound_ports = {
    443 : { "name" : "HTTPS", "rule_offset" : 0 }
    80 : { "name" : "HTTP", "rule_offset" : 10 }
  }
}

# tfsec:ignore:aws-ec2-no-public-ingress-acl
resource "aws_network_acl_rule" "public_ipv6_subnet_ipv4_ingress" {
  for_each       = local.inbound_ports
  network_acl_id = aws_network_acl.public_ipv6_subnets.id
  rule_number    = 100 + each.value["rule_offset"]
  rule_action    = "allow"
  egress         = false
  protocol       = "tcp"
  cidr_block     = "0.0.0.0/0"
  from_port      = each.key
  to_port        = each.key
}

# tfsec:ignore:aws-ec2-no-public-ingress-acl
resource "aws_network_acl_rule" "public_ipv6_subnet_ipv6_ingress" {
  for_each        = local.inbound_ports
  network_acl_id  = aws_network_acl.public_ipv6_subnets.id
  rule_number     = 200 + each.value["rule_offset"]
  rule_action     = "allow"
  egress          = false
  protocol        = "tcp"
  ipv6_cidr_block = "::/0"
  from_port       = each.key
  to_port         = each.key
}

#tfsec:ignore:aws-ec2-no-excessive-port-access
resource "aws_network_acl_rule" "public_ipv6_subnet_ipv4_egress" {
  network_acl_id = aws_network_acl.public_ipv6_subnets.id
  rule_number    = 100
  rule_action    = "allow"
  egress         = true
  protocol       = "all"
  cidr_block     = "0.0.0.0/0"
  from_port      = 0
  to_port        = 0
}

#tfsec:ignore:aws-ec2-no-excessive-port-access
resource "aws_network_acl_rule" "public_ipv6_subnet_ipv6_egress" {
  network_acl_id  = aws_network_acl.public_ipv6_subnets.id
  rule_number     = 200
  rule_action     = "allow"
  egress          = true
  protocol        = "all"
  ipv6_cidr_block = "::/0"
  from_port       = 0
  to_port         = 0
}

resource "aws_network_acl_association" "public_dualstack_subnet" {
  network_acl_id = aws_network_acl.public_ipv6_subnets.id
  subnet_id      = aws_subnet.gs_subnet_public.id
}

resource "aws_network_acl_association" "public_dualstack_subnet_2" {
  network_acl_id = aws_network_acl.public_ipv6_subnets.id
  subnet_id      = aws_subnet.gs_subnet_public_2.id
}