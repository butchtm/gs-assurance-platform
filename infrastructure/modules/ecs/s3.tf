# tfsec:ignore:aws-s3-enable-bucket-encryption
# tfsec:ignore:aws-s3-encryption-customer-key
#trivy:ignore:AVD-AWS-0089
# tfsec:ignore:aws-s3-enable-versioning
resource "aws_s3_bucket" "frontend_logs_bucket" {
  # checkov:skip=CKV_AWS_18: "Don't need logs for a log bucket"
  # checkov:skip=CKV_AWS_21: "Don't need versioning for a log bucket"

  #Future Considerations:
  # checkov:skip=CKV_AWS_145: "Skip encrypting S3 bucket with KMS for now, something to consider later"

  bucket = "assurance-frontend-log-bucket-${var.environment}"
}

resource "aws_s3_bucket_lifecycle_configuration" "frontend_logbucket_lifecycle" {
  bucket = aws_s3_bucket.frontend_logs_bucket.id

  rule {
    id     = "log"
    status = "Enabled"

    abort_incomplete_multipart_upload {
      days_after_initiation = 7
    }

    transition {
      days          = 30
      storage_class = "STANDARD_IA"
    }

    transition {
      days          = 60
      storage_class = "GLACIER"
    }
  }
}

data "aws_iam_policy_document" "alb_log_policy" {
  statement {
    actions   = ["s3:PutObject"]
    resources = ["${aws_s3_bucket.frontend_logs_bucket.arn}/*"]

    principals {
      type        = "Service"
      identifiers = ["delivery.logs.amazonaws.com"]
    }

    principals {
      type        = "AWS"
      identifiers = ["arn:aws:iam::054676820928:root"] #frankfurt alb
    }
  }
}


resource "aws_s3_bucket_policy" "alb_log_bucket_policy" {
  bucket = aws_s3_bucket.frontend_logs_bucket.id
  policy = data.aws_iam_policy_document.alb_log_policy.json
}

resource "aws_s3_bucket_public_access_block" "frontend_logs_bucket_access_block" {
  bucket = aws_s3_bucket.frontend_logs_bucket.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# tfsec:ignore:aws-s3-encryption-customer-key
#trivy:ignore:AVD-AWS-0089
# tfsec:ignore:aws-s3-enable-versioning
resource "aws_s3_bucket" "private_documents_bucket" {
  # checkov:skip=CKV_AWS_18: "Don't need logs for documents bucket"
  # checkov:skip=CKV_AWS_21: "Don't need versioning for documents bucket"
  # checkov:skip=CKV2_AWS_61: "Lifecycle configuration not necessary"

  #Future Considerations:
  # checkov:skip=CKV_AWS_145: "Skip encrypting S3 bucket with KMS for now, something to consider later"

  bucket = "gs-ssrnc-dcmts-${var.environment}"
  tags = {
    Name = "gs-ssrnc-dcmts-${var.environment}"
  }
}

# tfsec:ignore:aws-s3-encryption-customer-key
resource "aws_s3_bucket_server_side_encryption_configuration" "private_documents_bucket_encryption" {
  bucket = aws_s3_bucket.private_documents_bucket.id

  rule {
    bucket_key_enabled = true
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_public_access_block" "private_documents_bucket_access_block" {
  bucket = aws_s3_bucket.private_documents_bucket.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# tfsec:ignore:aws-s3-encryption-customer-key
#trivy:ignore:AVD-AWS-0089
# tfsec:ignore:aws-s3-enable-versioning
resource "aws_s3_bucket" "private_downloads_bucket" {
  # checkov:skip=CKV_AWS_18: "Don't need logs for downloads bucket"
  # checkov:skip=CKV_AWS_21: "Don't need versioning for downloads bucket"

  #Future Considerations:
  # checkov:skip=CKV_AWS_145: "Skip encrypting S3 bucket with KMS for now, something to consider later"

  bucket = "gs-ssrnc-dwlds-${var.environment}"
  tags = {
    Name = "gs-ssrnc-dwlds-${var.environment}"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "private_downloads_bucket_lifecycle" {
  bucket = aws_s3_bucket.private_downloads_bucket.id

  rule {
    id     = "expire_all_files"
    status = "Enabled"

    abort_incomplete_multipart_upload {
      days_after_initiation = 7
    }

    expiration {
      days = 8
    }
  }
}

# tfsec:ignore:aws-s3-encryption-customer-key
resource "aws_s3_bucket_server_side_encryption_configuration" "private_downloads_bucket_encryption" {
  bucket = aws_s3_bucket.private_downloads_bucket.id

  rule {
    bucket_key_enabled = true
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_public_access_block" "private_downloads_bucket_access_block" {
  bucket = aws_s3_bucket.private_downloads_bucket.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# tfsec:ignore:aws-s3-encryption-customer-key
#trivy:ignore:AVD-AWS-0089
# tfsec:ignore:aws-s3-enable-versioning
resource "aws_s3_bucket" "private_scanning_bucket" {
  # checkov:skip=CKV_AWS_18: "Don't need logs for scanning bucket"
  # checkov:skip=CKV_AWS_21: "Don't need versioning for scanning bucket"

  #Future Considerations:
  # checkov:skip=CKV_AWS_145: "Skip encrypting S3 bucket with KMS for now, something to consider later"

  bucket = "gs-ssrnc-scan-${var.environment}"
  tags = {
    Name = "gs-ssrnc-scan-${var.environment}"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "private_scanning_bucket_lifecycle" {
  bucket = aws_s3_bucket.private_scanning_bucket.id

  rule {
    id     = "expire_all_files"
    status = "Enabled"

    abort_incomplete_multipart_upload {
      days_after_initiation = 7
    }

    expiration {
      days = 1
    }
  }
}

# tfsec:ignore:aws-s3-encryption-customer-key
resource "aws_s3_bucket_server_side_encryption_configuration" "private_scanning_bucket_encryption" {
  bucket = aws_s3_bucket.private_scanning_bucket.id

  rule {
    bucket_key_enabled = true
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_public_access_block" "private_scanning_bucket_access_block" {
  bucket = aws_s3_bucket.private_scanning_bucket.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}