resource "aws_acm_certificate" "goldstandard-cert" {
  for_each          = var.acm_certificates
  domain_name       = each.value.domain_name
  validation_method = "DNS"
  lifecycle {
    create_before_destroy = true
  }
}
