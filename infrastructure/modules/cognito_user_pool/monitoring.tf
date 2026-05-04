variable "cognito_monitoring_alarmlist" {
  type = map(object({
    name              = string,
    metric_name       = string,
    alarm_description = string,
  }))
  default = {
    cognito_sign_up_throttle = {
      name : "cognito-sign-up-throttle-"
      metric_name : "SignUpThrottles"
      alarm_description : "Alarm when more than 5 sign ups in a 60 second period"
    }
    cognito_sign_in_throttle = {
      name : "cognito-sign-in-throttle-"
      metric_name : "SignInThrottles"
      alarm_description : "Alarm when more than 5 sign ins in a 60 second period"
    }
    cognito_refresh_token_throttle = {
      name : "cognito-token-refresh-throttle-"
      metric_name : "TokenRefreshThrottles"
      alarm_description : "Alarm when more than 5 Token Refresh in a 60 second period"
    }
  }
}

resource "aws_cloudwatch_metric_alarm" "cognito_monitoring_throttles" {
  for_each            = var.cognito_monitoring_alarmlist
  alarm_name          = "${each.value.name}${var.environment}"
  comparison_operator = "GreaterThanThreshold"
  datapoints_to_alarm = 5
  evaluation_periods  = 5
  metric_name         = each.value.metric_name
  namespace           = "AWS/Cognito"
  statistic           = "Sum"
  threshold           = 1
  period              = 60
  treat_missing_data  = "notBreaching"
  alarm_description   = each.value.alarm_description
  alarm_actions       = [var.monitoring_topic_arn]

  dimensions = {
    UserPool       = aws_cognito_user_pool.gs_pool.arn
    UserPoolClient = aws_cognito_user_pool_client.gs_assurance_backend_client.user_pool_id
  }
}