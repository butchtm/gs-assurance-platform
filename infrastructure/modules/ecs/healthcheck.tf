#tfsec:ignore:aws-sns-enable-topic-encryption
resource "aws_sns_topic" "health_check_topic" {
  #checkov:skip=CKV_AWS_26: "Encryption not necessary for CloudWatch alarms"
  name = "health-check-topic-${var.environment}"
}

resource "aws_sns_topic_subscription" "user_updates_sqs_target" {
  for_each  = var.monitoring_notifications_list
  topic_arn = aws_sns_topic.health_check_topic.arn
  protocol  = "email"
  endpoint  = each.value
}


// Front end alarms

resource "aws_cloudwatch_metric_alarm" "health_check_fe_tg_unhealthy_alarm" {
  alarm_name          = "fe-unhealthy-host-${var.environment}"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "UnHealthyHostCount"
  namespace           = "AWS/ApplicationELB"
  statistic           = "Maximum"
  threshold           = 0
  period              = 60
  alarm_description   = "This metric monitors for any unhealthy hosts in the front end target group"
  alarm_actions       = [aws_sns_topic.health_check_topic.arn]

  dimensions = {
    LoadBalancer = aws_lb.frontend_alb.arn_suffix
    TargetGroup  = aws_lb_target_group.frontend_targetgroup.arn_suffix
  }
}

resource "aws_cloudwatch_metric_alarm" "health_check_fe_tg_healthy_alarm" {
  alarm_name                = "fe-insufficient-healthy-hosts-${var.environment}"
  comparison_operator       = "LessThanThreshold"
  evaluation_periods        = 1
  metric_name               = "HealthyHostCount"
  namespace                 = "AWS/ApplicationELB"
  statistic                 = "Minimum"
  threshold                 = 1
  period                    = 60
  alarm_description         = "This metric monitors for sufficient healthy hosts in the front end target group"
  alarm_actions             = [aws_sns_topic.health_check_topic.arn]
  insufficient_data_actions = [aws_sns_topic.health_check_topic.arn]

  dimensions = {
    LoadBalancer = aws_lb.frontend_alb.arn_suffix
    TargetGroup  = aws_lb_target_group.frontend_targetgroup.arn_suffix
  }
}

// Back end alarms

resource "aws_cloudwatch_metric_alarm" "health_check_be_tg_unhealthy_alarm" {
  alarm_name          = "be-unhealthy-host-${var.environment}"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "UnHealthyHostCount"
  namespace           = "AWS/ApplicationELB"
  statistic           = "Maximum"
  threshold           = 0
  period              = 60
  alarm_description   = "This metric monitors for any unhealthy hosts in the back end target group"
  alarm_actions       = [aws_sns_topic.health_check_topic.arn]

  dimensions = {
    LoadBalancer = aws_lb.frontend_alb.arn_suffix
    TargetGroup  = aws_lb_target_group.backend_targetgroup.arn_suffix
  }
}

resource "aws_cloudwatch_metric_alarm" "health_check_be_tg_healthy_alarm" {
  alarm_name                = "be-insufficient-healthy-hosts-${var.environment}"
  comparison_operator       = "LessThanThreshold"
  evaluation_periods        = 1
  metric_name               = "HealthyHostCount"
  namespace                 = "AWS/ApplicationELB"
  statistic                 = "Minimum"
  threshold                 = 1
  period                    = 60
  alarm_description         = "This metric monitors for sufficient healthy hosts in the back end target group"
  alarm_actions             = [aws_sns_topic.health_check_topic.arn]
  insufficient_data_actions = [aws_sns_topic.health_check_topic.arn]


  dimensions = {
    LoadBalancer = aws_lb.frontend_alb.arn_suffix
    TargetGroup  = aws_lb_target_group.backend_targetgroup.arn_suffix
  }
}