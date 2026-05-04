variable "gs_assurance_db_identifier" {
  description = "The identifier for the GS Assurance DB"
  default     = "gs-assurance-db"
}

resource "aws_cloudwatch_dashboard" "assurance_monitoring_dashboard" {
  dashboard_name = "assurance-monitoring-dashboard"

  dashboard_body = jsonencode({
    "widgets" : [
      {
        "height" : 7,
        "width" : 10,
        "y" : 19,
        "x" : 14,
        "type" : "metric",
        "properties" : {
          "metrics" : [
            ["AWS/RDS", "CPUUtilization", "DBInstanceIdentifier", "${var.gs_assurance_db_identifier}", { "id" : "m1", "visible" : false }],
            [".", "FreeStorageSpace", ".", ".", { "id" : "m2", "color" : "#9467bd" }],
            [".", "FreeableMemory", ".", ".", { "id" : "m3", "color" : "#c5b0d5" }]
          ],
          "sparkline" : true,
          "view" : "singleValue",
          "region" : "${var.region}",
          "stat" : "Average",
          "period" : 60,
          "title" : "RDS Storage"
        }
      },
      {
        "height" : 7,
        "width" : 9,
        "y" : 19,
        "x" : 5,
        "type" : "metric",
        "properties" : {
          "sparkline" : true,
          "view" : "timeSeries",
          "metrics" : [
            ["AWS/RDS", "CPUUtilization", "DBInstanceIdentifier", "${var.gs_assurance_db_identifier}", { "period" : 60, "region" : "${var.region}" }]
          ],
          "region" : "${var.region}",
          "title" : "RDS CPU Utilisation",
          "period" : 300,
          "stacked" : false
        }
      },
      {
        "height" : 9,
        "width" : 13,
        "y" : 10,
        "x" : 0,
        "type" : "metric",
        "properties" : {
          "metrics" : [
            ["AWS/ApplicationELB", "TargetResponseTime", "TargetGroup", "${module.ecs.frontend_target_group_arn_suffix}", "LoadBalancer", "${module.ecs.load_balancer_arn_suffix}", { "region" : "${var.region}", "color" : "#2ca02c", "label" : "Front-end" }],
            ["...", "${module.ecs.backend_target_group_arn_suffix}", ".", ".", { "region" : "${var.region}", "label" : "Back-end" }]
          ],
          "view" : "timeSeries",
          "stacked" : false,
          "region" : "${var.region}",
          "title" : "Target Response Time",
          "stat" : "Average",
          "period" : 60,
          "yAxis" : {
            "left" : {
              "showUnits" : false,
              "label" : ""
            }
          }
        }
      },
      {
        "height" : 10,
        "width" : 9,
        "y" : 0,
        "x" : 5,
        "type" : "metric",
        "properties" : {
          "metrics" : [
            ["AWS/ECS", "CPUUtilization", "ServiceName", "${module.ecs.ecs_frontend_service_name}", "ClusterName", "${module.ecs.ecs_cluster_name}", { "region" : "${var.region}", "color" : "#2ca02c" }]
          ],
          "view" : "timeSeries",
          "stacked" : false,
          "region" : "${var.region}",
          "period" : 300,
          "title" : "Front-end CPU Utilisation",
          "stat" : "Average"
        }
      },
      {
        "height" : 10,
        "width" : 10,
        "y" : 0,
        "x" : 14,
        "type" : "metric",
        "properties" : {
          "metrics" : [
            ["AWS/ECS", "CPUUtilization", "ServiceName", "${module.ecs.ecs_backend_service_name}", "ClusterName", "${module.ecs.ecs_cluster_name}", { "region" : "${var.region}", "color" : "#ff7f0e" }]
          ],
          "view" : "timeSeries",
          "stacked" : false,
          "region" : "${var.region}",
          "title" : "Back-end CPU Utilisation",
          "period" : 300,
          "stat" : "Average"
        }
      },
      {
        "height" : 3,
        "width" : 6,
        "y" : 16,
        "x" : 13,
        "type" : "metric",
        "properties" : {
          "metrics" : [
            ["AWS/ApplicationELB", "HTTPCode_Target_5XX_Count", "LoadBalancer", "${module.ecs.load_balancer_arn_suffix}", { "region" : "${var.region}", "color" : "#d62728" }]
          ],
          "view" : "singleValue",
          "stacked" : false,
          "region" : "${var.region}",
          "title" : "5XX Count",
          "period" : 300,
          "stat" : "Average"
        }
      },
      {
        "height" : 7,
        "width" : 5,
        "y" : 0,
        "x" : 0,
        "type" : "alarm",
        "properties" : {
          "title" : "ECS Health",
          "alarms" : [
            "${module.ecs.frontend_cpu_alarm_arn}",
            "${module.ecs.backend_cpu_alarm_arn}",
            "${module.ecs.frontend_unhealthy_host_alarm_arn}",
            "${module.ecs.frontend_healthy_host_alarm_arn}",
            "${module.ecs.backend_unhealthy_host_alarm_arn}",
            "${module.ecs.backend_healthy_host_alarm_arn}",
          ]
        }
      },
      {
        "height" : 3,
        "width" : 5,
        "y" : 7,
        "x" : 0,
        "type" : "alarm",
        "properties" : {
          "title" : "Response Time",
          "alarms" : [
            "${module.ecs.frontend_response_time_alarm_arn}",
            "${module.ecs.backend_response_time_alarm_arn}",
          ]
        }
      },
      {
        "height" : 7,
        "width" : 5,
        "y" : 19,
        "x" : 0,
        "type" : "alarm",
        "properties" : {
          "title" : "Database",
          "alarms" : [
            "${module.rds.db_cpu_alarm_arn}",
            "${module.rds.db_freeable_memory_alarm_arn}",
            "${module.rds.db_storage_space_alarm_arn}",
          ]
        }
      },
      {
        "height" : 6,
        "width" : 11,
        "y" : 10,
        "x" : 13,
        "type" : "metric",
        "properties" : {
          "metrics" : [
            ["BackendLogGroup", "ErrorCount", { "color" : "#d62728" }]
          ],
          "view" : "timeSeries",
          "stacked" : false,
          "region" : "${var.region}",
          "stat" : "Sum",
          "period" : 60,
          "title" : "Back-end Log Errors"
        }
      }
    ]
  })
}

resource "aws_cloudwatch_dashboard" "assurance_uptime_dashboard" {
  dashboard_name = "assurance-uptime-dashboard"
  dashboard_body = jsonencode({
    "start" : "-PT2160H",
    "widgets" : [
      {
        "height" : 13,
        "width" : 5,
        "y" : 0,
        "x" : 6,
        "type" : "metric",
        "properties" : {
          "metrics" : [
            ["AWS/ApplicationELB", "HealthyHostCount", "TargetGroup", "${module.ecs.backend_target_group_arn_suffix}", "LoadBalancer", "${module.ecs.load_balancer_arn_suffix}", { "region" : "eu-central-1", "color" : "#ff7f0e", "label" : "% of time with sufficient healthy hosts" }]
          ],
          "view" : "gauge",
          "region" : "eu-central-1",
          "title" : "Back-end 30 day availability",
          "yAxis" : {
            "left" : {
              "min" : 0,
              "max" : 100
            }
          },
          "legend" : {
            "position" : "bottom"
          },
          "stat" : "PR(0.99:)",
          "period" : 2592000
        }
      },
      {
        "height" : 13,
        "width" : 6,
        "y" : 0,
        "x" : 0,
        "type" : "metric",
        "properties" : {
          "metrics" : [
            ["AWS/ApplicationELB", "HealthyHostCount", "TargetGroup", "${module.ecs.frontend_target_group_arn_suffix}", "LoadBalancer", "${module.ecs.load_balancer_arn_suffix}", { "color" : "#2ca02c", "region" : "eu-central-1", "label" : "% of time with sufficient healthy hosts" }]
          ],
          "view" : "gauge",
          "region" : "eu-central-1",
          "yAxis" : {
            "left" : {
              "min" : 0,
              "max" : 100
            }
          },
          "stat" : "PR(0.99:)",
          "period" : 2592000,
          "title" : "Front-end 30 day availability"
        }
      },
      {
        "height" : 13,
        "width" : 12,
        "y" : 0,
        "x" : 11,
        "type" : "metric",
        "properties" : {
          "metrics" : [
            ["AWS/ApplicationELB", "HealthyHostCount", "TargetGroup", "${module.ecs.frontend_target_group_arn_suffix}", "LoadBalancer", "app/fe-application-lb-development/ac26352a5d65f85e", { "label" : "Front-end", "color" : "#2ca02c", "region" : "eu-central-1" }],
            ["...", "${module.ecs.backend_target_group_arn_suffix}", ".", ".", { "label" : "Back-end", "region" : "eu-central-1" }]
          ],
          "view" : "timeSeries",
          "stacked" : false,
          "title" : "Running daily availability",
          "region" : "eu-central-1",
          "stat" : "PR(0.99:)",
          "period" : 86400
        }
      }
    ]
  })
}
