#
# IAM policies for ECS Tasks
#
resource "aws_iam_policy" "backend_db_pwd" {
  name        = "gs_ecs_backend_db_pwd-${var.environment}"
  description = "Allow (backend) ECS tasks to retrieve db credentials from Secrets Manager"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action   = "secretsmanager:GetSecretValue"
        Effect   = "Allow"
        Resource = data.aws_secretsmanager_secret.backend_db_pwd.arn
      }
    ]
  })
}

resource "aws_iam_policy" "allow_list_cognito_users" {
  name        = "gs_ecs_backend_allow_list_cognito_users-${var.environment}"
  description = "Allow (backend) ECS tasks to list and create Cognito UserPool users"

  policy = jsonencode({
    Version : "2012-10-17"
    "Statement" : [
      {
        Action : [
          "cognito-idp:ListUsers",
          "cognito-idp:GetUser",
          "cognito-idp:ListGroups",
          "cognito-idp:ListUsersInGroup",
          "cognito-idp:AdminCreateUser",
          "cognito-idp:AdminAddUserToGroup",
          "cognito-idp:AdminUpdateUserAttributes",
          "cognito-idp:AdminRemoveUserFromGroup",
          "cognito-idp:AdminListGroupsForUser",
          "cognito-idp:AdminGetUser",
          "cognito-idp:AdminSetUserMFAPreference",
        ]
        Effect : "Allow"
        Resource : "arn:aws:cognito-idp:${var.region}:${data.aws_caller_identity.current.account_id}:userpool/${var.cognito_pool_id}"
      }
    ]
  })
}

# tfsec:ignore:aws-iam-no-policy-wildcards
resource "aws_iam_policy" "backend_document_access" {
  name        = "gs_ecs_backend_document_access-${var.environment}"
  description = "Allow (backend) ECS tasks write access to the S3 document storage"

  # Policy is restricted to the documents bucket only for the backend
  # TODO: See if we can lock this down further but essentially backend needs rw accesss
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action   = "s3:ListBucket"
        Effect   = "Allow"
        Resource = "arn:aws:s3:::gs-ssrnc-dcmts-${var.environment}"
      },
      {
        Action   = "s3:*Object*"
        Effect   = "Allow"
        Resource = "arn:aws:s3:::gs-ssrnc-dcmts-${var.environment}/*"
      }
    ]
  })
}

# tfsec:ignore:aws-iam-no-policy-wildcards
resource "aws_iam_policy" "backend_download_access" {
  name        = "gs_ecs_backend_download_access-${var.environment}"
  description = "Allow (backend) ECS tasks write access to the S3 download storage"

  # Policy is restricted to the downloads bucket only for the backend
  # TODO: See if we can lock this down further but essentially backend needs rw accesss
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action   = "s3:*Object*"
        Effect   = "Allow"
        Resource = "arn:aws:s3:::gs-ssrnc-dwlds-${var.environment}/*"
      }
    ]
  })
}

# tfsec:ignore:aws-iam-no-policy-wildcards
resource "aws_iam_policy" "backend_scanning_access" {
  name        = "gs_ecs_backend_scanning_access-${var.environment}"
  description = "Allow (backend) ECS tasks write access to the S3 scanning storage"

  # Policy is restricted to the scanning bucket only for the backend
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "s3:GetObject",
          "s3:ListBucket",
          "s3:PutObject"
        ],
        Effect = "Allow"
        Resource = [
          "arn:aws:s3:::gs-ssrnc-scan-${var.environment}",
          "arn:aws:s3:::gs-ssrnc-scan-${var.environment}/*"
        ]
      }
    ]
  })
}

resource "aws_iam_policy" "allow_send_email" {
  name        = "gs_ecs_backend_allow_send_email-${var.environment}"
  description = "Allow (backend) ECS tasks to send SES emails"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action   = "ses:SendEmail"
        Effect   = "Allow"
        Resource = "arn:aws:ses:${var.region}:${data.aws_caller_identity.current.account_id}:identity/*"
      }
    ]
  })
}


# tfsec:ignore:AVD-AWS-0342
resource "aws_iam_policy" "allow_detect_pii_entities" {
  name        = "gs_ecs_backend_allow_detect_pii_entities-${var.environment}"
  description = "Allow (backend) ECS tasks to detect pii entities"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action   = "comprehend:DetectPiiEntities"
        Effect   = "Allow"
        Resource = "*"
      },
      {
        Action = [
          "comprehend:StartPiiEntitiesDetectionJob",
          "comprehend:DescribePiiEntitiesDetectionJob"
        ]
        Effect   = "Allow"
        Resource = "arn:aws:comprehend:*:*:pii-entities-detection-job/*"
      },
      {
        Action   = "iam:PassRole",
        Effect   = "Allow",
        Resource = "${aws_iam_role.comprehend_pii_detection_role.arn}"
      }
    ]
  })
}

resource "aws_iam_policy" "allow_document_text_detection" {
  name        = "gs_ecs_backend_allow_document_text_detection-${var.environment}"
  description = "Allow (backend) ECS tasks to start and get document text detection"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "textract:StartDocumentTextDetection",
          "textract:GetDocumentTextDetection"
        ]
        Effect   = "Allow"
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_policy" "allow_sns_publish" {
  name        = "gs_ecs_backend_allow_sns_publish-${var.environment}"
  description = "Allow (backend) ECS tasks to publish to SNS"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "sns:publish",
        ]
        Effect   = "Allow"
        Resource = "arn:aws:sns:${var.region}:${data.aws_caller_identity.current.account_id}:health-check-topic-${var.environment}"
      }
    ]
  })
}

#
# Define front-end and back-end roles
#
locals {
  ecs_task_roles = toset([
    "frontend_task_role",
    "backend_task_role"
  ])
  ecs_task_execution_roles = toset([
    "frontend_task_execution_role",
    "backend_task_execution_role"
  ])
  ecs_roles = setunion(local.ecs_task_roles, local.ecs_task_execution_roles)
}

resource "aws_iam_role" "ecs_roles" {
  for_each = local.ecs_roles
  name     = "${each.key}-${var.environment}"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = ""
        Action = "sts:AssumeRole"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
        Effect = "Allow"
      }
    ]
  })
}

locals {
  ecs_task_execution_roles_arns = { for k, v in aws_iam_role.ecs_roles : k => v if contains(local.ecs_task_execution_roles, k) }
}

#
# Role for Comprehend PII entity detection jobs
#


resource "aws_iam_role" "comprehend_pii_detection_role" {
  name = "comprehend-pii-detection-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "comprehend.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_policy" "comprehend_s3_access_policy" {
  name        = "comprehend-s3-access-policy"
  description = "Allow S3 access to gs-ssrnc-scan-${var.environment} bucket"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "s3:GetObject",
          "s3:ListBucket",
          "s3:PutObject"
        ],
        Resource = [
          "arn:aws:s3:::gs-ssrnc-scan-${var.environment}",
          "arn:aws:s3:::gs-ssrnc-scan-${var.environment}/*"
        ]
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "attach_comprehend_s3_policy" {
  role       = aws_iam_role.comprehend_pii_detection_role.name
  policy_arn = aws_iam_policy.comprehend_s3_access_policy.arn
}

#
# Common policies for the execution roles
#
resource "aws_iam_role_policy_attachment" "task_execution" {
  for_each   = local.ecs_task_execution_roles_arns
  role       = each.value.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}
resource "aws_iam_role_policy_attachment" "cloudmap" {
  for_each   = local.ecs_task_execution_roles_arns
  role       = each.value.name
  policy_arn = aws_iam_policy.ecs_cloudmap_policy.arn
}

#
# Back-end-specific task execution role policies
#
resource "aws_iam_role_policy_attachment" "backend_db_pwd" {
  role       = aws_iam_role.ecs_roles["backend_task_execution_role"].name
  policy_arn = aws_iam_policy.backend_db_pwd.arn
}

#
# Back-end-specific task role policies
#
resource "aws_iam_role_policy_attachment" "backend_allow_list_cognito_users" {
  role       = aws_iam_role.ecs_roles["backend_task_role"].name
  policy_arn = aws_iam_policy.allow_list_cognito_users.arn
}
resource "aws_iam_role_policy_attachment" "backend_document_access" {
  role       = aws_iam_role.ecs_roles["backend_task_role"].name
  policy_arn = aws_iam_policy.backend_document_access.arn
}
resource "aws_iam_role_policy_attachment" "backend_download_access" {
  role       = aws_iam_role.ecs_roles["backend_task_role"].name
  policy_arn = aws_iam_policy.backend_download_access.arn
}
resource "aws_iam_role_policy_attachment" "backend_scanning_access" {
  role       = aws_iam_role.ecs_roles["backend_task_role"].name
  policy_arn = aws_iam_policy.backend_scanning_access.arn
}
resource "aws_iam_role_policy_attachment" "backend_allow_send_email" {
  role       = aws_iam_role.ecs_roles["backend_task_role"].name
  policy_arn = aws_iam_policy.allow_send_email.arn
}
resource "aws_iam_role_policy_attachment" "backend_allow_detect_pii_entities" {
  role       = aws_iam_role.ecs_roles["backend_task_role"].name
  policy_arn = aws_iam_policy.allow_detect_pii_entities.arn
}
resource "aws_iam_role_policy_attachment" "backend_allow_document_text_detection" {
  role       = aws_iam_role.ecs_roles["backend_task_role"].name
  policy_arn = aws_iam_policy.allow_document_text_detection.arn
}
resource "aws_iam_role_policy_attachment" "backend_allow_sns_publish" {
  role       = aws_iam_role.ecs_roles["backend_task_role"].name
  policy_arn = aws_iam_policy.allow_sns_publish.arn
}


resource "aws_security_group" "frontend_alb_allow_http_s" {
  # checkov:skip=CKV_AWS_260: "Port 80 is forwarded and this security group is used on an internal ALB"
  name        = "frontend_allow_http_s-${var.environment}"
  description = "Allow HTTP and HTTPS traffic from Cloudfront to private ALB"
  vpc_id      = var.aws_vpc
}

# Source group for front end
resource "aws_security_group" "frontend" {
  name        = "frontend_sg-${var.environment}"
  description = "Allow HTTP inbound from the ALB: Permissive outbound for now"
  vpc_id      = var.aws_vpc
}

# Source group for back end
resource "aws_security_group" "backend" {
  name        = "backend_sg-${var.environment}"
  description = "Allow HTTP inbound from the frontend: Permissive outbound for now"
  vpc_id      = var.aws_vpc
}

#
# ALB SG rules
#

resource "aws_vpc_security_group_ingress_rule" "alb_http_inbound" {
  security_group_id = aws_security_group.frontend_alb_allow_http_s.id
  for_each          = var.enable_application_ip_restriction ? var.application_allowlist.ipv4 : { "0.0.0.0/0" : "Allow all IPV4" }
  description       = "Enable incoming HTTP traffic: ${each.value}"
  from_port         = 80
  to_port           = 80
  ip_protocol       = "tcp"
  cidr_ipv4         = each.key
  tags              = { "Name" : "${each.value}: HTTP" }
}

resource "aws_vpc_security_group_ingress_rule" "alb_https_inbound" {
  security_group_id = aws_security_group.frontend_alb_allow_http_s.id
  for_each          = var.enable_application_ip_restriction ? var.application_allowlist.ipv4 : { "0.0.0.0/0" : "Allow all IPV4" }
  description       = "Enable incoming HTTPS traffic: ${each.value}"
  from_port         = 443
  to_port           = 443
  ip_protocol       = "tcp"
  cidr_ipv4         = each.key
  tags              = { "Name" : "${each.value}: HTTPS" }
}

resource "aws_vpc_security_group_ingress_rule" "alb_http_inbound_ipv6" {
  security_group_id = aws_security_group.frontend_alb_allow_http_s.id
  for_each          = var.enable_application_ip_restriction ? var.application_allowlist.ipv6 : { "::/0" : "Allow all IPV6" }
  description       = "Enable incoming HTTP traffic: ${each.value}"
  from_port         = 80
  to_port           = 80
  ip_protocol       = "tcp"
  cidr_ipv6         = each.key
  tags              = { "Name" : "${each.value}: HTTP" }
}

resource "aws_vpc_security_group_ingress_rule" "alb_https_inbound_ipv6" {
  security_group_id = aws_security_group.frontend_alb_allow_http_s.id
  for_each          = var.enable_application_ip_restriction ? var.application_allowlist.ipv6 : { "::/0" : "Allow all IPV6" }
  description       = "Enable incoming HTTPS traffic: ${each.value}"
  from_port         = 443
  to_port           = 443
  ip_protocol       = "tcp"
  cidr_ipv6         = each.key
  tags              = { "Name" : "${each.value}: HTTPS" }
}

resource "aws_vpc_security_group_egress_rule" "alb_https_outbound" {
  security_group_id            = aws_security_group.frontend_alb_allow_http_s.id
  description                  = "Enable outbound HTTP to the front end"
  from_port                    = 8080
  to_port                      = 8080
  ip_protocol                  = "tcp"
  referenced_security_group_id = aws_security_group.frontend.id
}

#
# Frontend SG rules
#

resource "aws_vpc_security_group_ingress_rule" "frontend_alb_inbound" {
  # checkov:skip=CKV_AWS_260: "Port 80 from ALB only"
  security_group_id            = aws_security_group.frontend.id
  description                  = "Allow ALB inbound http"
  from_port                    = 80
  to_port                      = 80
  ip_protocol                  = "tcp"
  referenced_security_group_id = aws_security_group.frontend_alb_allow_http_s.id
  tags                         = { "Name" : "allow-alb-inbound" }
}

resource "aws_vpc_security_group_ingress_rule" "frontend_alb_inbound_8080" {
  # checkov:skip=CKV_AWS_260: "Port 8080 from ALB only"
  security_group_id            = aws_security_group.frontend.id
  description                  = "Allow ALB inbound http on 8080"
  from_port                    = 8080
  to_port                      = 8080
  ip_protocol                  = "tcp"
  referenced_security_group_id = aws_security_group.frontend_alb_allow_http_s.id
  tags                         = { "Name" : "allow-alb-inbound" }
}

# tfsec:ignore:aws-ec2-no-public-egress-sgr
resource "aws_vpc_security_group_egress_rule" "frontend_all_outbound" {
  security_group_id = aws_security_group.frontend.id
  ip_protocol       = -1
  cidr_ipv4         = "0.0.0.0/0"
  tags              = { "Name" : "allow-all-outbound" }
  description       = "Allow all outbound"
}

#
# Backend SG rules
#

resource "aws_vpc_security_group_egress_rule" "alb_to_backend" {
  security_group_id            = aws_security_group.frontend_alb_allow_http_s.id
  description                  = "Enable outbound HTTP to the backend"
  from_port                    = 8080
  to_port                      = 8080
  ip_protocol                  = "tcp"
  referenced_security_group_id = aws_security_group.backend.id
  tags                         = { "Name" : "alb-to-backend" }
}

resource "aws_vpc_security_group_ingress_rule" "backend_from_alb" {
  security_group_id            = aws_security_group.backend.id
  description                  = "Enable inbound HTTP from the ALB"
  from_port                    = 8080
  to_port                      = 8080
  ip_protocol                  = "tcp"
  referenced_security_group_id = aws_security_group.frontend_alb_allow_http_s.id
  tags                         = { "Name" : "backend-from-alb" }
}

resource "aws_vpc_security_group_ingress_rule" "backend_frontend_inbound" {
  # checkov:skip=CKV_AWS_260: "Port 80 from frontend only over vpc"
  security_group_id            = aws_security_group.backend.id
  from_port                    = 8080
  to_port                      = 8080
  ip_protocol                  = "tcp"
  referenced_security_group_id = aws_security_group.frontend.id
  tags                         = { "Name" : "allow-frontend-inbound" }
  description                  = "Allow Frontend inbound http"
}

# tfsec:ignore:aws-ec2-no-public-egress-sgr
resource "aws_vpc_security_group_egress_rule" "backend_all_outbound" {
  security_group_id = aws_security_group.backend.id
  ip_protocol       = -1
  cidr_ipv4         = "0.0.0.0/0"
  tags              = { "Name" : "allow-all-outbound" }
  description       = "Allow all outbound"
}

