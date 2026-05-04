output "db_cpu_alarm_arn" {
  description = "Database CPU alarm ARN"
  value       = aws_cloudwatch_metric_alarm.rds_monitoring_alarms["rds_cpu_utlisation"].arn
}

output "db_freeable_memory_alarm_arn" {
  description = "Database freeable memory alarm ARN"
  value       = aws_cloudwatch_metric_alarm.rds_monitoring_alarms["rds_freeable_memory"].arn
}

output "db_storage_space_alarm_arn" {
  description = "Database storage space alarm ARN"
  value       = aws_cloudwatch_metric_alarm.rds_monitoring_alarms["rds_free_storage_space"].arn
}