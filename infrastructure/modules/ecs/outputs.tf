output "backend_sg_id" {
  sensitive   = false
  description = "backend security group id"
  value       = aws_security_group.backend.id
}

output "private_document_bucket" {
  sensitive   = false
  description = "s3 document storage"
  value       = aws_s3_bucket.private_documents_bucket
}

output "monitoring_topic_arn" {
  sensitive   = false
  description = "sns monitoring topic"
  value       = aws_sns_topic.health_check_topic.arn
}

output "frontend_target_group_arn_suffix" {
  description = "ARN suffix for frontend target group"
  value       = aws_lb_target_group.frontend_targetgroup.arn_suffix
}

output "backend_target_group_arn_suffix" {
  description = "ARN suffix for backend target group"
  value       = aws_lb_target_group.backend_targetgroup.arn_suffix
}

output "load_balancer_arn_suffix" {
  description = "ARN suffix for load balancer"
  value       = aws_lb.frontend_alb.arn_suffix
}

output "ecs_cluster_name" {
  description = "ECS Cluster name"
  value       = aws_ecs_cluster.main_ecs_cluster.name
}

output "ecs_frontend_service_name" {
  description = "Frontend ECS service name"
  value       = aws_ecs_service.frontend_ecs.name
}
output "ecs_backend_service_name" {
  description = "Backend ECS service name"
  value       = aws_ecs_service.backend_ecs.name
}

output "frontend_cpu_alarm_arn" {
  description = "Frontend CPU alarm ARN"
  value       = aws_cloudwatch_metric_alarm.ecs_monitoring_cpu_utlisation_frontend_alarm.arn
}

output "backend_cpu_alarm_arn" {
  description = "Backend CPU alarm ARN"
  value       = aws_cloudwatch_metric_alarm.ecs_monitoring_cpu_utlisation_backend_alarm.arn
}

output "frontend_unhealthy_host_alarm_arn" {
  description = "Frontend unhealthy host alarm ARN"
  value       = aws_cloudwatch_metric_alarm.health_check_fe_tg_unhealthy_alarm.arn
}

output "frontend_healthy_host_alarm_arn" {
  description = "Frontend healthy host alarm ARN"
  value       = aws_cloudwatch_metric_alarm.health_check_fe_tg_healthy_alarm.arn
}

output "backend_unhealthy_host_alarm_arn" {
  description = "Backend unhealthy host alarm ARN"
  value       = aws_cloudwatch_metric_alarm.health_check_be_tg_unhealthy_alarm.arn
}

output "backend_healthy_host_alarm_arn" {
  description = "Backend healthy host alarm ARN"
  value       = aws_cloudwatch_metric_alarm.health_check_be_tg_healthy_alarm.arn
}

output "frontend_response_time_alarm_arn" {
  description = "Frontend response time alarm ARN"
  value       = aws_cloudwatch_metric_alarm.alb_target_response_time_frontend_alarm.arn
}

output "backend_response_time_alarm_arn" {
  description = "Backend response time alarm ARN"
  value       = aws_cloudwatch_metric_alarm.alb_target_response_time_backend_alarm.arn
}

output "load_balancer_5xx_response_alarm_arn" {
  description = "5XX response alarm ARN"
  value       = aws_cloudwatch_metric_alarm.alb_http_5xx_alarm.arn
}
