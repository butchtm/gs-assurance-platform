variable "app_name" {
  type = string
}

variable "environment" {
  type = string
}

variable "acm_certificates" {}

variable "route53_zone" {}

variable "route53_records" {}

variable "route53_alias_records" {}
variable "azure_ip_address" {}
variable "azure_cidr_blocks" {}