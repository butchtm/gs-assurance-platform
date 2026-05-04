resource "aws_cloudwatch_metric_alarm" "ses_bounce_rate_alarm" {
  alarm_name          = "ses-bounce-rate-alarm-${var.environment}"
  alarm_description   = "Triggers when SES bounce rate exceeds 5%"
  namespace           = "AWS/SES"
  metric_name         = "BounceRate"
  statistic           = "Average"
  period              = 60
  evaluation_periods  = 5
  threshold           = 0.05 # 5% 
  treat_missing_data  = "notBreaching"
  comparison_operator = "GreaterThanOrEqualToThreshold"
  alarm_actions       = [var.monitoring_topic_arn]
}

resource "aws_cloudwatch_metric_alarm" "ses_complaint_rate_alarm" {
  alarm_name          = "ses-complaint-rate-alarm-${var.environment}"
  alarm_description   = "Triggers when SES complaint rate exceeds 0.1%"
  namespace           = "AWS/SES"
  metric_name         = "ComplaintRate"
  statistic           = "Average"
  period              = 60
  evaluation_periods  = 5
  threshold           = 0.001 # 0.1%
  treat_missing_data  = "notBreaching"
  comparison_operator = "GreaterThanOrEqualToThreshold"
  alarm_actions       = [var.monitoring_topic_arn]
}
