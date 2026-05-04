resource "aws_cloudwatch_metric_alarm" "alb_http_5xx_alarm" {
  alarm_name          = "alb-http-5xx-count-${var.environment}"
  metric_name         = "HTTPCode_ELB_5XX_Count"
  namespace           = "AWS/ApplicationELB"
  statistic           = "Sum"
  threshold           = 1
  period              = 60
  datapoints_to_alarm = 5
  evaluation_periods  = 5
  comparison_operator = "GreaterThanThreshold"
  treat_missing_data  = "notBreaching"
  alarm_description   = "This metric alarms when more than 5 HTTP 5XX errors in a 60 second period"
  alarm_actions       = [aws_sns_topic.health_check_topic.arn]

  dimensions = {
    LoadBalancer = aws_lb.frontend_alb.arn_suffix
  }
}

resource "aws_cloudwatch_metric_alarm" "alb_target_response_time_frontend_alarm" {
  alarm_name          = "alb-target-response-time-frontend-${var.environment}"
  metric_name         = "TargetResponseTime"
  namespace           = "AWS/ApplicationELB"
  statistic           = "Average"
  threshold           = 1
  period              = 60
  datapoints_to_alarm = 5
  evaluation_periods  = 5
  comparison_operator = "GreaterThanThreshold"
  alarm_description   = "This metric alarms when the frontend ALB response time is greater than 1s"
  alarm_actions       = [aws_sns_topic.health_check_topic.arn]

  dimensions = {
    LoadBalancer = aws_lb.frontend_alb.arn_suffix
    TargetGroup  = aws_lb_target_group.frontend_targetgroup.arn_suffix
  }
}

resource "aws_cloudwatch_metric_alarm" "alb_target_response_time_backend_alarm" {
  alarm_name          = "alb-target-response-time-backend-${var.environment}"
  metric_name         = "TargetResponseTime"
  namespace           = "AWS/ApplicationELB"
  statistic           = "Average"
  threshold           = 2
  period              = 60
  datapoints_to_alarm = 5
  evaluation_periods  = 5
  comparison_operator = "GreaterThanThreshold"
  alarm_description   = "This metric alarms when the backend ALB response time is greater than 2s"
  alarm_actions       = [aws_sns_topic.health_check_topic.arn]

  dimensions = {
    LoadBalancer = aws_lb.frontend_alb.arn_suffix
    TargetGroup  = aws_lb_target_group.backend_targetgroup.arn_suffix
  }
}

resource "aws_cloudwatch_metric_alarm" "ecs_monitoring_cpu_utlisation_frontend_alarm" {
  alarm_name          = "ecs-monitoring-cpu-utlisation-frontend-${var.environment}"
  metric_name         = "CPUUtilization"
  namespace           = "AWS/ECS"
  statistic           = "Average"
  threshold           = 90
  period              = 60
  datapoints_to_alarm = 5
  evaluation_periods  = 5
  comparison_operator = "GreaterThanThreshold"
  alarm_description   = "Alarm when CPU Utilisation is greater than 90%"
  alarm_actions       = [aws_sns_topic.health_check_topic.arn]

  dimensions = {
    ClusterName = aws_ecs_cluster.main_ecs_cluster.name
    ServiceName = aws_ecs_service.frontend_ecs.name
  }
}

resource "aws_cloudwatch_metric_alarm" "ecs_monitoring_cpu_utlisation_backend_alarm" {
  alarm_name          = "ecs-monitoring-cpu-utilisation-backend-${var.environment}"
  metric_name         = "CPUUtilization"
  namespace           = "AWS/ECS"
  statistic           = "Average"
  threshold           = 90
  period              = 60
  datapoints_to_alarm = 5
  evaluation_periods  = 5
  comparison_operator = "GreaterThanThreshold"
  alarm_description   = "Alarm when CPU Utilisation is greater than 90%"
  alarm_actions       = [aws_sns_topic.health_check_topic.arn]

  dimensions = {
    ClusterName = aws_ecs_cluster.main_ecs_cluster.name
    ServiceName = aws_ecs_service.backend_ecs.name
  }
}
