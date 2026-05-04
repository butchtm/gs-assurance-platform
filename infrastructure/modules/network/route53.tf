resource "aws_route53_zone" "gold_standard_zone" {
  #checkov:skip=CKV2_AWS_39:"Logging not required"
  #checkov:skip=CKV2_AWS_38:"DNSSEC not required"
  for_each = var.route53_zone
  name     = each.value.domain_name
}

resource "aws_route53_record" "gold_standard_record" {
  for_each = var.route53_records
  name     = each.value.record_name
  ttl      = each.value.ttl
  type     = each.value.type
  zone_id  = "Z0578876234A4N2X44ARJ"
  records  = each.value.records
}

resource "aws_route53_record" "gold_standard_alias_record" {
  #checkov:skip=CKV2_AWS_23:"Resource is attached, checkov not detecting it"
  for_each = var.route53_alias_records
  name     = each.value.record_name
  type     = "A"
  zone_id  = "Z0578876234A4N2X44ARJ"
  alias {
    name                   = each.value.alias_name
    zone_id                = each.value.alias_id
    evaluate_target_health = true
  }
}