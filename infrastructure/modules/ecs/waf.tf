variable "rules" {
  type = list(any)
  default = [
    {
      name         = "fe-web-acl-ip-reputation-rule"
      priority     = 2
      managed_rule = "AWSManagedRulesAmazonIpReputationList"
    },
    {
      name         = "fe-web-acl-ip-anon-rule"
      priority     = 3
      managed_rule = "AWSManagedRulesAnonymousIpList"
    },
    {
      name         = "fe-web-acl-sql-rule"
      priority     = 5
      managed_rule = "AWSManagedRulesSQLiRuleSet"
    },
    {
      name         = "fe-web-acl-admin-protection-rule"
      priority     = 6
      managed_rule = "AWSManagedRulesAdminProtectionRuleSet"
    },
    {
      name         = "fe-web-acl-bad-inputs-rule"
      priority     = 7
      managed_rule = "AWSManagedRulesKnownBadInputsRuleSet"
    },
    {
      name         = "fe-web-acl-bot-control-rule"
      priority     = 8
      managed_rule = "AWSManagedRulesBotControlRuleSet"
    },
  ]
}

resource "aws_wafv2_web_acl" "frontend_web_acl" {
  # checkov:skip=CKV2_AWS_31:"Should add later - logging configuration"
  # checkov:skip=CKV_AWS_192: "Bad inputs are covered by above rule iteration"
  name  = "fe-web-acl-${var.environment}"
  scope = "REGIONAL"

  visibility_config {
    cloudwatch_metrics_enabled = false
    metric_name                = "fe-web-acl-metric-${var.environment}"
    sampled_requests_enabled   = false
  }

  default_action {
    dynamic "block" {
      for_each = var.enable_application_ip_restriction ? [""] : []
      content {}
    }
    dynamic "allow" {
      for_each = var.enable_application_ip_restriction ? [] : [""]
      content {}
    }
  }

  rule {
    name     = "fe-web-acl-common-rule-${var.environment}"
    priority = 4

    override_action {
      none {}
    }

    statement {
      managed_rule_group_statement {
        name        = "AWSManagedRulesCommonRuleSet"
        vendor_name = "AWS"
        rule_action_override {
          action_to_use {
            allow {}
          }
          name = "SizeRestrictions_BODY"
        }
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = false
      metric_name                = "fe-web-acl-common-rule-metric-${var.environment}"
      sampled_requests_enabled   = false
    }
  }

  dynamic "rule" {
    for_each = toset(var.rules)
    content {
      name     = "${rule.value.name}-${var.environment}"
      priority = rule.value.priority

      override_action {
        none {}
      }

      statement {
        managed_rule_group_statement {
          name        = rule.value.managed_rule
          vendor_name = "AWS"
        }
      }

      visibility_config {
        cloudwatch_metrics_enabled = false
        metric_name                = "${rule.value.name}-metric-${var.environment}"
        sampled_requests_enabled   = false
      }
    }
  }
  dynamic "rule" {
    for_each = var.enable_application_ip_restriction ? [""] : []
    content {
      name     = "fe-web-acl-ip-set-rule-${var.environment}"
      priority = 1

      action {
        allow {}
      }

      statement {
        or_statement {
          statement {
            ip_set_reference_statement {
              arn = aws_wafv2_ip_set.frontend_web_acl_ipv4_set.arn
            }
          }

          statement {
            ip_set_reference_statement {
              arn = aws_wafv2_ip_set.frontend_web_acl_ipv6_set.arn
            }
          }
        }
      }

      visibility_config {
        cloudwatch_metrics_enabled = false
        metric_name                = "fe-web-acl-ip-set-rule-metric-${var.environment}"
        sampled_requests_enabled   = false
      }
    }
  }

}

resource "aws_wafv2_ip_set" "frontend_web_acl_ipv4_set" {
  name               = "fe-web-acl-ipv4-set-${var.environment}"
  scope              = "REGIONAL"
  ip_address_version = "IPV4"
  addresses          = keys(var.application_allowlist.ipv4)
}

resource "aws_wafv2_ip_set" "frontend_web_acl_ipv6_set" {
  name               = "fe-web-acl-ipv6-set-${var.environment}"
  scope              = "REGIONAL"
  ip_address_version = "IPV6"
  addresses          = keys(var.application_allowlist.ipv6)
}

resource "aws_wafv2_web_acl_association" "frontend_web_acl_association" {
  resource_arn = aws_lb.frontend_alb.arn
  web_acl_arn  = aws_wafv2_web_acl.frontend_web_acl.arn
}

# tfsec:ignore:aws-cloudwatch-log-group-customer-key
resource "aws_cloudwatch_log_group" "frontend_web_acl_log_group" {
  #checkov:skip=CKV_AWS_158: "Ensure that CloudWatch Log Group is encrypted by KMS"
  name              = "aws-waf-logs-fe-web-acl"
  retention_in_days = 365
}

resource "aws_wafv2_web_acl_logging_configuration" "frontend_web_acl_logging" {
  log_destination_configs = [aws_cloudwatch_log_group.frontend_web_acl_log_group.arn]
  resource_arn            = aws_wafv2_web_acl.frontend_web_acl.arn
}

resource "aws_iam_role" "frontend_web_acl_log_role" {
  name = "frontend_web_acl_log_role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = ["sts:AssumeRole"]
        Effect = "Allow"
        Principal = {
          Service : "waf.amazonaws.com"
        }
      },
    ]

  })
}

resource "aws_iam_role_policy" "frontend_web_acl_log_policy" {
  name = "frontend_web_acl_log_policy"
  role = aws_iam_role.frontend_web_acl_log_role.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Effect   = "Allow"
        Resource = "${aws_cloudwatch_log_group.frontend_web_acl_log_group.arn}"
      },
    ]
  })
}
