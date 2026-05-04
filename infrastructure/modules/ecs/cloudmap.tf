data "aws_caller_identity" "current" {} #Gets current account id

resource "aws_service_discovery_private_dns_namespace" "assurance_services_namespace" {
  name = "assurance.service-${var.environment}"
  vpc  = var.aws_vpc
}

resource "aws_service_discovery_service" "backend_service_discovery" {
  name = "backend-api-${var.environment}"

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.assurance_services_namespace.id

    dns_records {
      ttl  = 15
      type = "A"
    }

    routing_policy = "MULTIVALUE"
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

resource "aws_service_discovery_service" "frontend_service_discovery" {
  name = "frontend-ui-${var.environment}"

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.assurance_services_namespace.id

    dns_records {
      ttl  = 15
      type = "A"
    }

    routing_policy = "MULTIVALUE"
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

#Add IAM role to allow ECS to attach cloud map

resource "aws_iam_policy" "ecs_cloudmap_policy" {
  name        = "ecs-cloudmap-policy-${var.environment}"
  description = "Policy to allow ECS to register and deregister with Cloud Map"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = [
          "servicediscovery:RegisterInstance",
          "servicediscovery:DeregisterInstance",
          "servicediscovery:Get*",
          "servicediscovery:List*"
        ],
        Resource = [
          "arn:aws:servicediscovery:eu-central-1:${data.aws_caller_identity.current.account_id}:namespace/${aws_service_discovery_private_dns_namespace.assurance_services_namespace.id}",
          "arn:aws:servicediscovery:eu-central-1:${data.aws_caller_identity.current.account_id}:service/${aws_service_discovery_service.backend_service_discovery.id}",
          "arn:aws:servicediscovery:eu-central-1:${data.aws_caller_identity.current.account_id}:service/${aws_service_discovery_service.frontend_service_discovery.id}"
        ],
        Effect = "Allow"
      }
    ]
  })
}
