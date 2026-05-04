data "aws_secretsmanager_secret" "backend_db_pwd" { name = var.rds_pw_secrets_name }

resource "aws_ecs_cluster" "main_ecs_cluster" {
  name = "assurance-services-${var.environment}"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = {
    Name = "assurance-services-ecs-cluster"
  }
}

resource "aws_ecs_cluster_capacity_providers" "example" {
  cluster_name = aws_ecs_cluster.main_ecs_cluster.name

  capacity_providers = ["FARGATE"]

  default_capacity_provider_strategy {
    base              = 1
    weight            = 100
    capacity_provider = "FARGATE"
  }
}

resource "aws_ecs_task_definition" "backend_api_td" {
  #checkov:skip=CKV_AWS_336: "Have ECS as non-readonly"
  family                   = "backend-api"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.backend_task_sizing["cpu"]
  memory                   = var.backend_task_sizing["memory"]
  execution_role_arn       = aws_iam_role.ecs_roles["backend_task_execution_role"].arn
  task_role_arn            = aws_iam_role.ecs_roles["backend_task_role"].arn

  container_definitions = jsonencode([
    {
      name   = "td-backend-api"
      image  = "${aws_ecr_repository.backend_api.repository_url}:${var.backend_image_tag}"
      cpu    = var.backend_task_sizing["cpu"]
      memory = var.backend_task_sizing["memory"]
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
        }
      ]
      readonlyRootFilesystem = false
      essential              = true
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-region"        = "eu-central-1"
          "awslogs-group"         = aws_cloudwatch_log_group.backend_api_logs.name
          "awslogs-stream-prefix" = "ecs"
        }
      }
      secrets = [
        {
          name      = "SPRING_DATASOURCE_PASSWORD"
          valueFrom = data.aws_secretsmanager_secret.backend_db_pwd.arn
        }
      ]
      environment = [
        {
          name  = "SERVER_PORT"
          value = "8080"
        },
        {
          name  = "SERVER_SERVLET_CONTEXTPATH"
          value = "/api"
        },
        {
          name  = "SPRING_DATASOURCE_USERNAME"
          value = "assurance_backend"
        },
        {
          name  = "SPRING_DATASOURCE_DRIVERCLASSNAME"
          value = "org.postgresql.Driver"
        },
        {
          name  = "SPRING_DATASOURCE_HIKARI_SCHEMA"
          value = "gs"
        },
        {
          name  = "SPRING_DATASOURCE_URL"
          value = "${var.rds_connection_string}"
        },
        {
          name  = "SPRING_JPA_DATABASEPLATFORM"
          value = "org.hibernate.dialect.PostgreSQLDialect"
        },
        {
          name  = "SPRING_JPA_PROPERTIES_HIBERNATE_DEFAULTSCHEMA"
          value = "gs"
        },
        {
          name  = "SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUERURI"
          value = "https://cognito-idp.${var.region}.amazonaws.com/${var.cognito_pool_id}"
        },
        # User sync config - where to sync from
        {
          name  = "COGNITO_REGION_NAME"
          value = "${var.region}"
        },
        {
          name  = "COGNITO_USER_POOL_ID"
          value = "${var.cognito_pool_id}"
        },
        {
          name  = "COGNITO_APP_CLIENT_ID"
          value = "${var.cognito_app_client_id}"
        },
        # User sync config - how often to sync
        {
          name  = "USER_SYNC_CHECK_CRON"
          value = "${var.user_sync_check_cron}"
        },
        {
          name  = "USER_SYNC_SYNC_INTERVAL"
          value = "${tostring(var.user_sync_sync_interval)}"
        },
        {
          name  = "NOTIFICATION_CONTACT_MAPPING_ENABLED"
          value = "${tostring(var.notification_contact_mapping_enabled)}"
        },
        {
          name  = "NOTIFICATION_SOURCE"
          value = "${var.notification_from_email}"
        },
        {
          name  = "S3_BUCKET_NAME_DOCUMENT"
          value = "gs-ssrnc-dcmts-${var.environment}"
        },
        {
          name  = "S3_BUCKET_NAME_DOWNLOAD"
          value = "gs-ssrnc-dwlds-${var.environment}"
        },
        {
          name  = "S3_BUCKET_NAME_PII_SCANNING"
          value = "gs-ssrnc-scan-${var.environment}"
        },
        {
          name  = "SPRING_PROFILES_ACTIVE"
          value = "${var.environment}"
        },
        {
          name  = "REGISTRY_PROJECT_API_HOST"
          value = "${var.registry_project_api_host}"
        },
        {
          name  = "REGISTRY_PROJECT_API_TOKEN"
          value = "${var.registry_project_api_token}"
        },
        {
          name  = "REGISTRY_PROJECT_API_LINK_TEMPLATE"
          value = "${var.registry_project_api_link_template}"
        },
        {
          name  = "REGISTRY_UI_HOST"
          value = "${var.registry_ui_host}"
        },
        {
          name  = "SDG_TOOL_PROJECT_API_HOST"
          value = "${var.sdg_tool_project_api_host}"
        },
        {
          name  = "SDG_TOOL_PROJECT_API_TOKEN"
          value = "${var.sdg_tool_project_api_token}"
        },
        {
          name  = "COMPREHEND_DATA_ACCESS_ROLE_ARN"
          value = "${aws_iam_role.comprehend_pii_detection_role.arn}"
        },
        {
          name  = "AZURE_STORAGE_FINANCE_CONNECTION_STRING"
          value = "${var.azure_storage_finance_connection_string}"
        },
        {
          name  = "WEB_UI_HOSTNAME"
          value = "${var.web_ui_hostname}"
        },
        {
          name  = "DOCUMENT_PII_SCANNING_ENABLED"
          value = "${tostring(var.document_pii_scanning_enabled)}"
        },
        {
          name  = "LOGGING_SNS_ARN"
          value = "${aws_sns_topic.health_check_topic.arn}"
        },
      ]
    }
  ])
}

resource "aws_ecs_task_definition" "frontend_ui_td" {
  #checkov:skip=CKV_AWS_336: "Have ECS as non-readonly"
  family                   = "frontend-ui"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.frontend_task_sizing["cpu"]
  memory                   = var.frontend_task_sizing["memory"]
  execution_role_arn       = aws_iam_role.ecs_roles["frontend_task_execution_role"].arn
  task_role_arn            = aws_iam_role.ecs_roles["frontend_task_role"].arn

  container_definitions = jsonencode([
    {
      name   = "td-frontend-ui"
      image  = "${aws_ecr_repository.frontend_ui.repository_url}:${var.frontend_image_tag}"
      cpu    = var.frontend_task_sizing["cpu"]
      memory = var.frontend_task_sizing["memory"]
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
        }
      ]
      readonlyRootFilesystem = false
      essential              = true
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-region"        = "eu-central-1"
          "awslogs-group"         = aws_cloudwatch_log_group.frontend_ui_logs.name
          "awslogs-stream-prefix" = "ecs"
        }
      }
      environment = [
        {
          name  = "PORT"
          value = "8080"
        },
        {
          name  = "PROXY_TARGET"
          value = "http://${aws_service_discovery_service.backend_service_discovery.name}.${aws_service_discovery_private_dns_namespace.assurance_services_namespace.name}:8080"
        },
        {
          name  = "VITE_ENVIRONMENT"
          value = var.environment
        },
        {
          name  = "NODE_ENV"
          value = "production"
        }
      ]
    }
  ])
}

#Backend ECS service
resource "aws_ecs_service" "backend_ecs" {
  name            = "assurance-api-2-${var.environment}"
  cluster         = aws_ecs_cluster.main_ecs_cluster.id
  task_definition = aws_ecs_task_definition.backend_api_td.arn
  launch_type     = "FARGATE"
  desired_count   = var.backend_task_sizing["containers"]
  #iam_role        = aws_iam_role.foo.arn
  #  depends_on      = [aws_iam_role_policy.foo]
  depends_on = [aws_ecs_task_definition.backend_api_td]

  service_registries {
    registry_arn = aws_service_discovery_service.backend_service_discovery.arn
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.backend_targetgroup.arn
    container_name   = "td-backend-api"
    container_port   = 8080
  }

  /* ordered_placement_strategy {
    type  = "binpack"
    field = "cpu"
  } */
  network_configuration {
    assign_public_ip = false
    #security_groups  = [aws_security_group.greenops_sg.id, aws_security_group.allow_ecs_to_ecr.id]
    subnets         = [var.private_subnet_id]
    security_groups = [aws_security_group.backend.id]
  }

}

#Frontend ECS service
resource "aws_ecs_service" "frontend_ecs" {
  name            = "assurance-ui-2-${var.environment}"
  cluster         = aws_ecs_cluster.main_ecs_cluster.id
  task_definition = aws_ecs_task_definition.frontend_ui_td.arn
  desired_count   = var.frontend_task_sizing["containers"]
  launch_type     = "FARGATE"
  #iam_role        = aws_iam_role.foo.arn
  #depends_on      = [aws_iam_role_policy.foo]
  depends_on = [aws_ecs_task_definition.frontend_ui_td]

  service_registries {
    registry_arn = aws_service_discovery_service.frontend_service_discovery.arn
  }

  /*   ordered_placement_strategy {
    type  = "binpack"
    field = "cpu"
  }
 */
  load_balancer {
    target_group_arn = aws_lb_target_group.frontend_targetgroup.arn
    container_name   = "td-frontend-ui"
    container_port   = 8080
  }

  network_configuration {
    assign_public_ip = false
    #security_groups  = [aws_security_group.greenops_sg.id, aws_security_group.allow_ecs_to_ecr.id]
    subnets         = [var.private_subnet_id]
    security_groups = [aws_security_group.frontend.id]
  }

}