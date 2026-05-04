# tfsec:ignore:aws-cloudwatch-log-group-customer-key
resource "aws_cloudwatch_log_group" "backend_api_logs" {
  # checkov:skip=CKV_AWS_158:"Need to add log encryption"
  name              = "backend-api-${var.environment}"
  retention_in_days = "90"
}

resource "aws_cloudwatch_log_metric_filter" "backend_api_log_error_filter" {
  name           = "backend-log-error-filter-${var.environment}"
  pattern        = "?ERROR"
  log_group_name = aws_cloudwatch_log_group.backend_api_logs.name

  metric_transformation {
    name      = "ErrorCount"
    namespace = "BackendLogGroup"
    value     = "1"
  }
}


# tfsec:ignore:aws-cloudwatch-log-group-customer-key
resource "aws_cloudwatch_log_group" "frontend_ui_logs" {
  # checkov:skip=CKV_AWS_158:"Need to add log encryption"
  name              = "frontend-ui-${var.environment}"
  retention_in_days = "90"
}
