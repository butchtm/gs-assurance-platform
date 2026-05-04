resource "aws_cognito_user_pool" "gs_pool" {
  name = var.user_pool_name

  alias_attributes = ["email"] #what the user can sign in with

  schema {
    attribute_data_type      = "String"
    developer_only_attribute = false
    mutable                  = true
    name                     = "given_name"
    required                 = true
    string_attribute_constraints {
      max_length = 32 # This is required to stop user pool being recreated
      min_length = 0
    }
  }

  schema {
    attribute_data_type      = "String"
    developer_only_attribute = false
    mutable                  = true
    name                     = "family_name"
    required                 = true
    string_attribute_constraints {
      max_length = 32 # This is required to stop user pool being recreated
      min_length = 0
    }
  }

  schema {
    attribute_data_type      = "String"
    developer_only_attribute = false
    mutable                  = true
    name                     = "country"
    required                 = false #aws doesn't support requiring custom attbs yet
    string_attribute_constraints {
      max_length = 32 # This is required to stop user pool being recreated
    }
  }

  auto_verified_attributes = ["email"]

  deletion_protection = "ACTIVE"

  mfa_configuration = "OPTIONAL"

  software_token_mfa_configuration {
    enabled = true
  }

  email_mfa_configuration {
    message = <<EOT
      Hello,

      You have requested a verification code to log in to the Gold Standard Assurance Platform.<br/><br/>

      Your code is {####}.<br/><br/>

      If you did not request this code, please contact the support team immediately.<br/><br/>

      You can access the platform here: https://${var.application_url}
      EOT

    subject = "Gold Standard Assurance Platform MFA Code"
  }

  account_recovery_setting {

    recovery_mechanism {
      name     = "verified_phone_number"
      priority = 1
    }

    recovery_mechanism {
      name     = "verified_email"
      priority = 2
    }
  }

  admin_create_user_config {
    allow_admin_create_user_only = false

    invite_message_template {
      email_subject = "Gold Standard Assurance Platform Temporary Password"
      sms_message   = "{username}, your temporary password for the Gold Standard Assurance Platform is {####}"
      email_message = <<EOT
Dear {username}, you have been set up with a new user account on the Gold Standard Assurance Platform.<br/><br/>
Your username is {username}<br/>
Your temporary password for initial login is {####}<br/><br/>
Upon login, you will be prompted to update this to a password of your choice. The platform can be accessed here https://${var.application_url}.<br/><br/>
Use of the platform signifies acceptance of the <a href="https://globalgoals.goldstandard.org/t-prereview-gsiq-platform-terms-of-use">GS IQ Platform Terms of Use</a>. The Gold Standard for the Global Goals <a href="https://globalgoals.goldstandard.org/t-prereview-terms-and-conditions/">Terms and Conditions</a> have also been updated.<br/><br/>
FAQs for the New Assurance Model and Management Process for Gold Standard Project Certification can be found <a href="https://goldstandardhelp.freshdesk.com/support/solutions/articles/44002562276-new-assurance-model-and-management-process-for-gold-standard-project-certification">here</a>.<br/><br/>
For projects that have migrated from the SustainCERT application, email notifications for project request updates shall be sent to the Project Developer Email (i.e. the main email contact included within the SustainCERT application). If you require this to be updated, please ask your Gold Standard Impact Registry Account Manager to contact us via <a href="mailto:help@goldstandard.org">help@goldstandard.org</a>.<br/><br/>
Thank you,<br/> 
Gold Standard 
      EOT
    }
  }

  email_configuration {
    email_sending_account  = "DEVELOPER"
    reply_to_email_address = var.notification_from_email
    source_arn             = var.assurance_email_arn
  }

  password_policy {
    minimum_length                   = 8
    require_lowercase                = true
    require_numbers                  = true
    require_symbols                  = true
    require_uppercase                = true
    temporary_password_validity_days = 120
  }

  user_pool_add_ons {
    advanced_security_mode = "ENFORCED"
  }

  verification_message_template {
    default_email_option = "CONFIRM_WITH_CODE"
    email_subject        = "Gold Standard Assurance Platform Forgotten Password"
    sms_message          = "Your verification code for the Gold Standard Assurance Platform is {####}"
    email_message        = <<EOT
Hello, you have requested a verification code to reset your password for the Gold Standard Assurance Platform.<br/><br/>
Your code is {####}<br/><br/>You will be prompted to update your password on login. If you have not requested this code please contact the support team immediately.
You can access the platform here https://${var.application_url}.
    EOT
  }
}

resource "aws_cognito_user_pool_client" "gs_assurance_backend_client" {
  name = "gs-assurance-backend"

  user_pool_id = aws_cognito_user_pool.gs_pool.id

  generate_secret = false

  refresh_token_validity = 1  # 1 day
  access_token_validity  = 1  # 1 hour
  id_token_validity      = 1  # 1 hour
  auth_session_validity  = 10 # 10 minutes

  prevent_user_existence_errors = "ENABLED"
  enable_token_revocation       = true

  read_attributes = [
    "email", "family_name", "given_name"
  ]

  write_attributes = [
    "email", "family_name", "given_name"
  ]

  explicit_auth_flows = [
    "ALLOW_USER_SRP_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH",
    "ALLOW_USER_PASSWORD_AUTH"
  ]
}
