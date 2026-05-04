variable "rds_monitoring_alarmlist" {
  type = map(object({
    name                = string,
    metric_name         = string,
    statistic           = string,
    threshold           = number,
    period              = number,
    datapoints_to_alarm = number,
    evaluation_periods  = number,
    comparison_operator = string,
    alarm_description   = string,
  }))
  default = {
    rds_cpu_utlisation = {
      name : "rds-cpu-utlisation-"
      metric_name : "CPUUtilization"
      statistic : "Average"
      threshold : 80
      period : 300
      datapoints_to_alarm : 3
      evaluation_periods : 3
      comparison_operator : "GreaterThanThreshold"
      alarm_description : ""
    }
    rds_freeable_memory = {
      name : "rds-freeable-memory-"
      metric_name : "FreeableMemory"
      statistic : "Average"
      threshold : 300000000 # 300MB in bytes
      period : 60
      datapoints_to_alarm : 5
      evaluation_periods : 5
      comparison_operator : "LessThanThreshold"
      alarm_description : ""
    }
    rds_free_storage_space = {
      name : "rds-free-storage-space-"
      metric_name : "FreeStorageSpace"
      statistic : "Minimum"
      threshold : 1000000000 # 1GB in bytes
      period : 60
      datapoints_to_alarm : 5
      evaluation_periods : 5
      comparison_operator : "LessThanThreshold"
      alarm_description : ""
    }
  }
}

resource "aws_cloudwatch_metric_alarm" "rds_monitoring_alarms" {
  for_each            = var.rds_monitoring_alarmlist
  alarm_name          = "${each.value.name}${var.environment}"
  metric_name         = each.value.metric_name
  namespace           = "AWS/RDS"
  statistic           = each.value.statistic
  threshold           = each.value.threshold
  period              = each.value.period
  datapoints_to_alarm = each.value.datapoints_to_alarm
  evaluation_periods  = each.value.evaluation_periods
  comparison_operator = each.value.comparison_operator
  alarm_description   = each.value.alarm_description
  alarm_actions       = [var.monitoring_topic_arn]

  dimensions = {
    DBInstanceIdentifier = "gs-assurance-db"
  }

}