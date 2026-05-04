#tfsec:ignore:aws-elb-alb-not-public
resource "aws_lb" "frontend_alb" {
  #checkov:skip=CKV2_AWS_76: "Ensure AWS ALB attached WAFv2 WebACL is configured with AMR for Log4j Vulnerability"
  name                       = "fe-application-lb-${var.environment}"
  internal                   = false
  load_balancer_type         = "application"
  security_groups            = [aws_security_group.frontend_alb_allow_http_s.id]
  subnets                    = [var.public_subnet_id, var.public_subnet_id_2]
  drop_invalid_header_fields = true
  enable_deletion_protection = true
  ip_address_type            = "dualstack"

  access_logs {
    bucket  = aws_s3_bucket.frontend_logs_bucket.bucket
    prefix  = "alb-logs"
    enabled = true
  }
}

resource "aws_lb_listener" "frontend_alb_listener" {
  #checkov:skip=CKV2_AWS_74: "Ensure AWS Load Balancers use strong ciphers"
  load_balancer_arn = aws_lb.frontend_alb.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type = "redirect"
    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}


resource "aws_lb_listener" "frontend" {
  load_balancer_arn = aws_lb.frontend_alb.arn
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-TLS13-1-2-2021-06"
  certificate_arn   = data.aws_acm_certificate.encryption_certificate.arn

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.frontend_targetgroup.arn
  }
}


resource "aws_lb_target_group" "frontend_targetgroup" {
  #checkov:skip=CKV_AWS_378: "Ensure AWS Load Balancer doesn't use HTTP protocol"
  name        = "fe-target-group-${var.environment}"
  port        = 80
  protocol    = "HTTP"
  vpc_id      = var.aws_vpc
  target_type = "ip"

  health_check {
    enabled             = true
    interval            = 60
    path                = "/"
    port                = "8080"
    protocol            = "HTTP"
    timeout             = 5
    healthy_threshold   = 3
    unhealthy_threshold = 3
  }
}

resource "aws_lb_target_group" "backend_targetgroup" {
  #checkov:skip=CKV_AWS_378: "Ensure AWS Load Balancer doesn't use HTTP protocol"
  name        = "be-target-group-${var.environment}"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = var.aws_vpc
  target_type = "ip"

  health_check {
    enabled             = true
    interval            = 60
    path                = "/api/actuator/health"
    port                = "8080"
    protocol            = "HTTP"
    timeout             = 5
    healthy_threshold   = 3
    unhealthy_threshold = 3
    matcher             = "200"
  }
}

resource "aws_lb_listener_rule" "alb_fe_api_listener_rule" {
  listener_arn = aws_lb_listener.frontend.arn
  priority     = 100

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.backend_targetgroup.arn
  }

  condition {
    path_pattern {
      values = ["/api/*"]
    }
  }
}

data "aws_acm_certificate" "encryption_certificate" { //referenced as a data block for now
  domain   = var.encryption_certificate_name
  statuses = ["ISSUED"]
}

