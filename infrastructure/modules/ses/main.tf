resource "aws_ses_domain_identity" "assurance_domain" {
  domain = "goldstandard.org"
}

resource "aws_ses_email_identity" "assurance_email" {
  email = var.notification_from_email
}
