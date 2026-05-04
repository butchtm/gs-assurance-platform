resource "aws_cognito_user_group" "gs_admin" {
  name         = "GS-Admin"
  user_pool_id = aws_cognito_user_pool.gs_pool.id
}

resource "aws_cognito_user_group" "gs_reviewer" {
  name         = "GS-Reviewer"
  user_pool_id = aws_cognito_user_pool.gs_pool.id
}

resource "aws_cognito_user_group" "project_developer" {
  name         = "Project-Developer"
  user_pool_id = aws_cognito_user_pool.gs_pool.id
}

resource "aws_cognito_user_group" "vvb" {
  name         = "VVB"
  user_pool_id = aws_cognito_user_pool.gs_pool.id
}

resource "aws_cognito_user_group" "arm_team" {
  name         = "ARM-Team"
  user_pool_id = aws_cognito_user_pool.gs_pool.id
}

resource "aws_cognito_user_group" "finance" {
  name         = "Finance"
  user_pool_id = aws_cognito_user_pool.gs_pool.id
}

resource "aws_cognito_user_group" "gs_registry" {
  name         = "GS-Registry"
  user_pool_id = aws_cognito_user_pool.gs_pool.id
}

resource "aws_cognito_user_group" "tac" {
  name         = "TAC"
  user_pool_id = aws_cognito_user_pool.gs_pool.id
}

resource "aws_cognito_user_group" "support" {
  name         = "Support"
  user_pool_id = aws_cognito_user_pool.gs_pool.id
}