# tfsec:ignore:aws-ecr-repository-customer-key
# tfsec:ignore:aws-ecr-enforce-immutable-repository
resource "aws_ecr_repository" "backend_api" {
  #checkov:skip=CKV_AWS_51: "Allow immutable tags i.e. latest"
  name = "ecr-backend-api-${var.environment}"
  image_tag_mutability = (
    var.environment == "development" ? "MUTABLE" : "IMMUTABLE"
  )

  image_scanning_configuration {
    scan_on_push = true
  }
  encryption_configuration {
    encryption_type = "KMS"
  }

}

# tfsec:ignore:aws-ecr-repository-customer-key
# tfsec:ignore:aws-ecr-enforce-immutable-repository
resource "aws_ecr_repository" "frontend_ui" {
  #checkov:skip=CKV_AWS_51: "Allow immutable tags i.e. latest"
  name = "ecr-frontend-ui-${var.environment}"
  image_tag_mutability = (
    var.environment == "development" ? "MUTABLE" : "IMMUTABLE"
  )

  image_scanning_configuration {
    scan_on_push = true
  }
  encryption_configuration {
    encryption_type = "KMS"
  }
}