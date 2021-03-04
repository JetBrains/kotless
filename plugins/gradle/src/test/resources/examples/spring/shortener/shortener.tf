provider "aws" {
  version = "2.70.0"
  profile = "kotless-jetbrains"
  region = "eu-west-1"
}

provider "aws" {
  alias = "us_east_1"
  version = "2.70.0"
  profile = "kotless-jetbrains"
  region = "us-east-1"
}

resource "aws_api_gateway_base_path_mapping" "shortener" {
  api_id = aws_api_gateway_rest_api.shortener.id
  domain_name = "spring.short.kotless.io"
  stage_name = aws_api_gateway_deployment.spring_shortener.stage_name
}

resource "aws_api_gateway_deployment" "spring_shortener" {
  depends_on = [aws_api_gateway_integration.css_shortener_css, aws_api_gateway_integration.favicon_apng, aws_api_gateway_integration.get, aws_api_gateway_integration.js_shortener_js, aws_api_gateway_integration.r_get, aws_api_gateway_integration.shorten_get]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  stage_name = "1"
  variables = {
    "deployed_at" = timestamp()
  }
  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_api_gateway_domain_name" "shortener" {
  certificate_arn = data.aws_acm_certificate.spring_short_kotless_io.arn
  domain_name = "spring.short.kotless.io"
}

resource "aws_api_gateway_integration" "css_shortener_css" {
  depends_on = [aws_api_gateway_resource.css_shortener_css]
  credentials = aws_iam_role.kotless_static_role.arn
  http_method = "GET"
  integration_http_method = "GET"
  resource_id = aws_api_gateway_resource.css_shortener_css.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  type = "AWS"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:s3:path/${aws_s3_bucket_object.eu_spring_short_s3_ktls_aws_intellij_net_static_css_shortener_css.bucket}/${aws_s3_bucket_object.eu_spring_short_s3_ktls_aws_intellij_net_static_css_shortener_css.key}"
}

resource "aws_api_gateway_integration" "favicon_apng" {
  depends_on = [aws_api_gateway_resource.favicon_apng]
  credentials = aws_iam_role.kotless_static_role.arn
  http_method = "GET"
  integration_http_method = "GET"
  resource_id = aws_api_gateway_resource.favicon_apng.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  type = "AWS"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:s3:path/${aws_s3_bucket_object.eu_spring_short_s3_ktls_aws_intellij_net_static_favicon_apng.bucket}/${aws_s3_bucket_object.eu_spring_short_s3_ktls_aws_intellij_net_static_favicon_apng.key}"
}

resource "aws_api_gateway_integration" "get" {
  depends_on = [aws_api_gateway_rest_api.shortener]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_rest_api.shortener.root_resource_id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.io_kotless_examples_page_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "js_shortener_js" {
  depends_on = [aws_api_gateway_resource.js_shortener_js]
  credentials = aws_iam_role.kotless_static_role.arn
  http_method = "GET"
  integration_http_method = "GET"
  resource_id = aws_api_gateway_resource.js_shortener_js.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  type = "AWS"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:s3:path/${aws_s3_bucket_object.eu_spring_short_s3_ktls_aws_intellij_net_static_js_shortener_js.bucket}/${aws_s3_bucket_object.eu_spring_short_s3_ktls_aws_intellij_net_static_js_shortener_js.key}"
}

resource "aws_api_gateway_integration" "r_get" {
  depends_on = [aws_api_gateway_resource.r]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_resource.r.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.io_kotless_examples_page_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "shorten_get" {
  depends_on = [aws_api_gateway_resource.shorten]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_resource.shorten.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.io_kotless_examples_page_0.arn}/invocations"
}

resource "aws_api_gateway_integration_response" "css_shortener_css" {
  depends_on = [aws_api_gateway_integration.css_shortener_css, aws_api_gateway_method_response.css_shortener_css]
  http_method = aws_api_gateway_method.css_shortener_css.http_method
  resource_id = aws_api_gateway_resource.css_shortener_css.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  status_code = "200"
  response_parameters = {
    "method.response.header.Content-Type" = "integration.response.header.Content-Type"
    "method.response.header.Content-Length" = "integration.response.header.Content-Length"
  }
}

resource "aws_api_gateway_integration_response" "favicon_apng" {
  depends_on = [aws_api_gateway_integration.favicon_apng, aws_api_gateway_method_response.favicon_apng]
  content_handling = "CONVERT_TO_BINARY"
  http_method = aws_api_gateway_method.favicon_apng.http_method
  resource_id = aws_api_gateway_resource.favicon_apng.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  status_code = "200"
  response_parameters = {
    "method.response.header.Content-Type" = "integration.response.header.Content-Type"
    "method.response.header.Content-Length" = "integration.response.header.Content-Length"
  }
}

resource "aws_api_gateway_integration_response" "js_shortener_js" {
  depends_on = [aws_api_gateway_integration.js_shortener_js, aws_api_gateway_method_response.js_shortener_js]
  http_method = aws_api_gateway_method.js_shortener_js.http_method
  resource_id = aws_api_gateway_resource.js_shortener_js.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  status_code = "200"
  response_parameters = {
    "method.response.header.Content-Type" = "integration.response.header.Content-Type"
    "method.response.header.Content-Length" = "integration.response.header.Content-Length"
  }
}

resource "aws_api_gateway_method" "css_shortener_css" {
  depends_on = [aws_api_gateway_resource.css_shortener_css]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.css_shortener_css.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
}

resource "aws_api_gateway_method" "favicon_apng" {
  depends_on = [aws_api_gateway_resource.favicon_apng]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.favicon_apng.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
}

resource "aws_api_gateway_method" "get" {
  depends_on = [aws_api_gateway_rest_api.shortener]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_rest_api.shortener.root_resource_id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
}

resource "aws_api_gateway_method" "js_shortener_js" {
  depends_on = [aws_api_gateway_resource.js_shortener_js]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.js_shortener_js.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
}

resource "aws_api_gateway_method" "r_get" {
  depends_on = [aws_api_gateway_resource.r]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.r.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
}

resource "aws_api_gateway_method" "shorten_get" {
  depends_on = [aws_api_gateway_resource.shorten]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.shorten.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
}

resource "aws_api_gateway_method_response" "css_shortener_css" {
  depends_on = [aws_api_gateway_method.css_shortener_css]
  http_method = aws_api_gateway_method.css_shortener_css.http_method
  resource_id = aws_api_gateway_resource.css_shortener_css.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  status_code = "200"
  response_parameters = {
    "method.response.header.Content-Type" = true
    "method.response.header.Content-Length" = true
  }
}

resource "aws_api_gateway_method_response" "favicon_apng" {
  depends_on = [aws_api_gateway_method.favicon_apng]
  http_method = aws_api_gateway_method.favicon_apng.http_method
  resource_id = aws_api_gateway_resource.favicon_apng.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  status_code = "200"
  response_parameters = {
    "method.response.header.Content-Type" = true
    "method.response.header.Content-Length" = true
  }
}

resource "aws_api_gateway_method_response" "js_shortener_js" {
  depends_on = [aws_api_gateway_method.js_shortener_js]
  http_method = aws_api_gateway_method.js_shortener_js.http_method
  resource_id = aws_api_gateway_resource.js_shortener_js.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  status_code = "200"
  response_parameters = {
    "method.response.header.Content-Type" = true
    "method.response.header.Content-Length" = true
  }
}

resource "aws_api_gateway_resource" "css" {
  depends_on = [aws_api_gateway_rest_api.shortener]
  parent_id = aws_api_gateway_rest_api.shortener.root_resource_id
  path_part = "css"
  rest_api_id = aws_api_gateway_rest_api.shortener.id
}

resource "aws_api_gateway_resource" "css_shortener_css" {
  depends_on = [aws_api_gateway_resource.css]
  parent_id = aws_api_gateway_resource.css.id
  path_part = "shortener.css"
  rest_api_id = aws_api_gateway_rest_api.shortener.id
}

resource "aws_api_gateway_resource" "favicon_apng" {
  depends_on = [aws_api_gateway_rest_api.shortener]
  parent_id = aws_api_gateway_rest_api.shortener.root_resource_id
  path_part = "favicon.apng"
  rest_api_id = aws_api_gateway_rest_api.shortener.id
}

resource "aws_api_gateway_resource" "js" {
  depends_on = [aws_api_gateway_rest_api.shortener]
  parent_id = aws_api_gateway_rest_api.shortener.root_resource_id
  path_part = "js"
  rest_api_id = aws_api_gateway_rest_api.shortener.id
}

resource "aws_api_gateway_resource" "js_shortener_js" {
  depends_on = [aws_api_gateway_resource.js]
  parent_id = aws_api_gateway_resource.js.id
  path_part = "shortener.js"
  rest_api_id = aws_api_gateway_rest_api.shortener.id
}

resource "aws_api_gateway_resource" "r" {
  depends_on = [aws_api_gateway_rest_api.shortener]
  parent_id = aws_api_gateway_rest_api.shortener.root_resource_id
  path_part = "r"
  rest_api_id = aws_api_gateway_rest_api.shortener.id
}

resource "aws_api_gateway_resource" "shorten" {
  depends_on = [aws_api_gateway_rest_api.shortener]
  parent_id = aws_api_gateway_rest_api.shortener.root_resource_id
  path_part = "shorten"
  rest_api_id = aws_api_gateway_rest_api.shortener.id
}

resource "aws_api_gateway_rest_api" "shortener" {
  binary_media_types = ["application/gzip", "application/zip", "font/ttf", "image/apng", "image/bmp", "image/gif", "image/jpeg", "image/png", "image/webp"]
  name = "spring-short-shortener"
}

resource "aws_cloudwatch_event_rule" "autowarm_io_kotless_examples_page_0" {
  name = "spring-short-autowarm-io-kotless-examples-page-0"
  schedule_expression = "cron(0/5 * * * ? *)"
}

resource "aws_cloudwatch_event_target" "autowarm_io_kotless_examples_page_0" {
  arn = aws_lambda_function.io_kotless_examples_page_0.arn
  rule = aws_cloudwatch_event_rule.autowarm_io_kotless_examples_page_0.name
}

resource "aws_iam_role" "io_kotless_examples_page_0" {
  assume_role_policy = data.aws_iam_policy_document.io_kotless_examples_page_0_assume.json
  name = "spring-short-io-kotless-examples-page-0"
}

resource "aws_iam_role" "kotless_static_role" {
  assume_role_policy = data.aws_iam_policy_document.kotless_static_assume.json
  name = "spring-short-kotless-static-role"
}

resource "aws_iam_role_policy" "io_kotless_examples_page_0" {
  policy = data.aws_iam_policy_document.io_kotless_examples_page_0.json
  role = aws_iam_role.io_kotless_examples_page_0.name
}

resource "aws_iam_role_policy" "kotless_static_policy" {
  policy = data.aws_iam_policy_document.kotless_static_policy.json
  role = aws_iam_role.kotless_static_role.name
}

resource "aws_lambda_function" "io_kotless_examples_page_0" {
  function_name = "spring-short-io-kotless-examples-page-0"
  handler = "io.kotless.examples.Application::handleRequest"
  memory_size = 1024
  role = aws_iam_role.io_kotless_examples_page_0.arn
  runtime = "java11"
  s3_bucket = "eu.spring-short.s3.ktls.aws.intellij.net"
  s3_key = "kotless-lambdas/spring-short-io-kotless-examples-page-0.jar"
  source_code_hash = filesha256(aws_s3_bucket_object.io_kotless_examples_page_0.source)
  timeout = 300
  environment {
    variables = {
      "KOTLESS_PACKAGES" = "io.kotless.examples"
    }
  }
}

resource "aws_lambda_permission" "autowarm_io_kotless_examples_page_0" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.io_kotless_examples_page_0.arn
  principal = "events.amazonaws.com"
  source_arn = aws_cloudwatch_event_rule.autowarm_io_kotless_examples_page_0.arn
  statement_id = "spring-short-autowarm-io-kotless-examples-page-0"
}

resource "aws_lambda_permission" "get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.io_kotless_examples_page_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.shortener.id}/*/GET/"
  statement_id = "spring-short-get"
}

resource "aws_lambda_permission" "r_get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.io_kotless_examples_page_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.shortener.id}/*/GET/r"
  statement_id = "spring-short-r-get"
}

resource "aws_lambda_permission" "shorten_get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.io_kotless_examples_page_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.shortener.id}/*/GET/shorten"
  statement_id = "spring-short-shorten-get"
}

resource "aws_route53_record" "spring_short_kotless_io" {
  name = "spring.short"
  type = "A"
  zone_id = data.aws_route53_zone.kotless_io.zone_id
  alias {
    evaluate_target_health = false
    name = aws_api_gateway_domain_name.shortener.cloudfront_domain_name
    zone_id = aws_api_gateway_domain_name.shortener.cloudfront_zone_id
  }
}

resource "aws_s3_bucket_object" "eu_spring_short_s3_ktls_aws_intellij_net_static_css_shortener_css" {
  bucket = "eu.spring-short.s3.ktls.aws.intellij.net"
  content_type = "text/css"
  etag = filemd5("{root}/spring/shortener/src/main/resources/static/css/shortener.css")
  key = "static/css/shortener.css"
  source = "{root}/spring/shortener/src/main/resources/static/css/shortener.css"
}

resource "aws_s3_bucket_object" "eu_spring_short_s3_ktls_aws_intellij_net_static_favicon_apng" {
  bucket = "eu.spring-short.s3.ktls.aws.intellij.net"
  content_type = "image/apng"
  etag = filemd5("{root}/spring/shortener/src/main/resources/static/favicon.apng")
  key = "static/favicon.apng"
  source = "{root}/spring/shortener/src/main/resources/static/favicon.apng"
}

resource "aws_s3_bucket_object" "eu_spring_short_s3_ktls_aws_intellij_net_static_js_shortener_js" {
  bucket = "eu.spring-short.s3.ktls.aws.intellij.net"
  content_type = "application/javascript"
  etag = filemd5("{root}/spring/shortener/src/main/resources/static/js/shortener.js")
  key = "static/js/shortener.js"
  source = "{root}/spring/shortener/src/main/resources/static/js/shortener.js"
}

resource "aws_s3_bucket_object" "io_kotless_examples_page_0" {
  bucket = "eu.spring-short.s3.ktls.aws.intellij.net"
  etag = filemd5("{root}/build/shortener/libs/shortener-0.1.7-beta-5-all.jar")
  key = "kotless-lambdas/spring-short-io-kotless-examples-page-0.jar"
  source = "{root}/build/shortener/libs/shortener-0.1.7-beta-5-all.jar"
}

data "aws_acm_certificate" "spring_short_kotless_io" {
  provider = aws.us_east_1
  domain = "spring.short.kotless.io"
  statuses = ["ISSUED"]
}

data "aws_caller_identity" "current" {
  
}

data "aws_iam_policy_document" "io_kotless_examples_page_0" {
  statement {
    actions = ["dynamodb:BatchGetItem", "dynamodb:BatchWriteItem", "dynamodb:Create*", "dynamodb:Delete*", "dynamodb:Describe*", "dynamodb:GetItem", "dynamodb:List*", "dynamodb:PutItem", "dynamodb:Query", "dynamodb:Restore*", "dynamodb:Scan", "dynamodb:TagResource", "dynamodb:TransactGetItems", "dynamodb:TransactWriteItems", "dynamodb:UntagResource", "dynamodb:Update*"]
    effect = "Allow"
    resources = ["arn:aws:dynamodb:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:table/spring-short-url-table"]
  }
  statement {
    actions = ["dynamodb:Query", "dynamodb:Scan"]
    effect = "Allow"
    resources = ["arn:aws:dynamodb:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:table/spring-short-url-table/index/*"]
  }
  statement {
    actions = ["logs:CreateLogGroup", "logs:CreateLogStream", "logs:DeleteLogGroup", "logs:DeleteLogStream", "logs:DeleteMetricFilter", "logs:DescribeLogGroups", "logs:DescribeLogStreams", "logs:DescribeMetricFilters", "logs:GetLogEvents", "logs:GetLogGroupFields", "logs:GetLogRecord", "logs:GetQueryResults", "logs:PutLogEvents", "logs:PutMetricFilter"]
    effect = "Allow"
    resources = ["arn:aws:logs:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:*"]
  }
}

data "aws_iam_policy_document" "io_kotless_examples_page_0_assume" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      identifiers = ["apigateway.amazonaws.com", "lambda.amazonaws.com"]
      type = "Service"
    }
  }
}

data "aws_iam_policy_document" "kotless_static_assume" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      identifiers = ["apigateway.amazonaws.com"]
      type = "Service"
    }
  }
}

data "aws_iam_policy_document" "kotless_static_policy" {
  statement {
    actions = ["s3:GetObject"]
    effect = "Allow"
    resources = ["${data.aws_s3_bucket.kotless_bucket.arn}/*"]
  }
}

data "aws_region" "current" {
  
}

data "aws_route53_zone" "kotless_io" {
  name = "kotless.io"
  private_zone = false
}

data "aws_s3_bucket" "kotless_bucket" {
  bucket = "eu.spring-short.s3.ktls.aws.intellij.net"
}

output "application_url" {
  value = "https://spring.short.kotless.io"
}

terraform {
  required_version = "0.12.29"
  backend "s3" {
    bucket = "eu.spring-short.s3.ktls.aws.intellij.net"
    key = "kotless-state/state.tfstate"
    profile = "kotless-jetbrains"
    region = "eu-west-1"
  }
}

