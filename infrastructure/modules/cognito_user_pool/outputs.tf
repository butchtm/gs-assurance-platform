output "cognito_user_pool_id" {
  value       = aws_cognito_user_pool.gs_pool.id
  description = "The ID of the Cognito User Pool."
}

output "cognito_app_client_id" {
  value       = aws_cognito_user_pool_client.gs_assurance_backend_client.id
  description = "The ID of the Cognito App Client."
}
