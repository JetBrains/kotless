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
  domain_name = "short.kotless.io"
  stage_name = aws_api_gateway_deployment.kotless_shortener.stage_name
}

resource "aws_api_gateway_deployment" "kotless_shortener" {
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
  certificate_arn = data.aws_acm_certificate.short_kotless_io.arn
  domain_name = "short.kotless.io"
}

resource "aws_api_gateway_integration" "css_shortener_css" {
  depends_on = [aws_api_gateway_resource.css_shortener_css]
  credentials = aws_iam_role.kotless_static_role.arn
  http_method = "GET"
  integration_http_method = "GET"
  resource_id = aws_api_gateway_resource.css_shortener_css.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  type = "AWS"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:s3:path/${aws_s3_bucket_object.eu_short_s3_ktls_aws_intellij_net_static_css_shortener_css.bucket}/${aws_s3_bucket_object.eu_short_s3_ktls_aws_intellij_net_static_css_shortener_css.key}"
}

resource "aws_api_gateway_integration" "favicon_apng" {
  depends_on = [aws_api_gateway_resource.favicon_apng]
  credentials = aws_iam_role.kotless_static_role.arn
  http_method = "GET"
  integration_http_method = "GET"
  resource_id = aws_api_gateway_resource.favicon_apng.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  type = "AWS"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:s3:path/${aws_s3_bucket_object.eu_short_s3_ktls_aws_intellij_net_static_favicon_apng.bucket}/${aws_s3_bucket_object.eu_short_s3_ktls_aws_intellij_net_static_favicon_apng.key}"
}

resource "aws_api_gateway_integration" "get" {
  depends_on = [aws_api_gateway_rest_api.shortener]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_rest_api.shortener.root_resource_id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "js_shortener_js" {
  depends_on = [aws_api_gateway_resource.js_shortener_js]
  credentials = aws_iam_role.kotless_static_role.arn
  http_method = "GET"
  integration_http_method = "GET"
  resource_id = aws_api_gateway_resource.js_shortener_js.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  type = "AWS"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:s3:path/${aws_s3_bucket_object.eu_short_s3_ktls_aws_intellij_net_static_js_shortener_js.bucket}/${aws_s3_bucket_object.eu_short_s3_ktls_aws_intellij_net_static_js_shortener_js.key}"
}

resource "aws_api_gateway_integration" "r_get" {
  depends_on = [aws_api_gateway_resource.r]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_resource.r.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "shorten_get" {
  depends_on = [aws_api_gateway_resource.shorten]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_resource.shorten.id
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
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
  binary_media_types = ["application/gzip", "application/zip", "font/ttf", "image/apng", "image/bmp", "image/gif", "image/jpeg", "image/png", "image/svg", "image/webp"]
  name = "short-shortener"
}

resource "aws_cloudwatch_event_rule" "autowarm_merged_0" {
  name = "short-autowarm-merged-0"
  schedule_expression = "cron(0/5 * * * ? *)"
}

resource "aws_cloudwatch_event_rule" "general_1525265728" {
  name = "short-general-1525265728"
  schedule_expression = "cron(0 0/1 * * ? *)"
}

resource "aws_cloudwatch_event_target" "autowarm_merged_0" {
  arn = aws_lambda_function.merged_0.arn
  rule = aws_cloudwatch_event_rule.autowarm_merged_0.name
}

resource "aws_cloudwatch_event_target" "general_1525265728" {
  arn = aws_lambda_function.merged_0.arn
  rule = aws_cloudwatch_event_rule.general_1525265728.name
}

resource "aws_iam_role" "kotless_static_role" {
  assume_role_policy = data.aws_iam_policy_document.kotless_static_assume.json
  name = "short-kotless-static-role"
}

resource "aws_iam_role" "merged_0" {
  assume_role_policy = data.aws_iam_policy_document.merged_0_assume.json
  name = "short-merged-0"
}

resource "aws_iam_role_policy" "kotless_static_policy" {
  policy = data.aws_iam_policy_document.kotless_static_policy.json
  role = aws_iam_role.kotless_static_role.name
}

resource "aws_iam_role_policy" "merged_0" {
  policy = data.aws_iam_policy_document.merged_0.json
  role = aws_iam_role.merged_0.name
}

resource "aws_lambda_function" "merged_0" {
  function_name = "short-merged-0"
  handler = "io.kotless.dsl.LambdaHandler::handleRequest"
  memory_size = 1024
  role = aws_iam_role.merged_0.arn
  runtime = "java11"
  s3_bucket = "eu.short.s3.ktls.aws.intellij.net"
  s3_key = "kotless-lambdas/short-merged-0.jar"
  source_code_hash = filesha256(aws_s3_bucket_object.merged_0.source)
  timeout = 300
  environment {
    variables = {
      "KOTLESS_PACKAGES" = "io.kotless.examples"
    }
  }
}

resource "aws_lambda_permission" "autowarm_merged_0" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "events.amazonaws.com"
  source_arn = aws_cloudwatch_event_rule.autowarm_merged_0.arn
  statement_id = "short-autowarm-merged-0"
}

resource "aws_lambda_permission" "general_1525265728" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "events.amazonaws.com"
  source_arn = aws_cloudwatch_event_rule.general_1525265728.arn
  statement_id = "short-general-1525265728"
}

resource "aws_lambda_permission" "get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.shortener.id}/*/GET/"
  statement_id = "short-get"
}

resource "aws_lambda_permission" "r_get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.shortener.id}/*/GET/r"
  statement_id = "short-r-get"
}

resource "aws_lambda_permission" "shorten_get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.shortener.id}/*/GET/shorten"
  statement_id = "short-shorten-get"
}

resource "aws_route53_record" "short_kotless_io" {
  name = "short"
  type = "A"
  zone_id = data.aws_route53_zone.kotless_io.zone_id
  alias {
    evaluate_target_health = false
    name = aws_api_gateway_domain_name.shortener.cloudfront_domain_name
    zone_id = aws_api_gateway_domain_name.shortener.cloudfront_zone_id
  }
}

resource "aws_s3_bucket_object" "eu_short_s3_ktls_aws_intellij_net_static_css_shortener_css" {
  bucket = "eu.short.s3.ktls.aws.intellij.net"
  content_type = "text/css"
  etag = filemd5("{root}/kotless/shortener/src/main/resources/css/shortener.css")
  key = "static/css/shortener.css"
  source = "{root}/kotless/shortener/src/main/resources/css/shortener.css"
}

resource "aws_s3_bucket_object" "eu_short_s3_ktls_aws_intellij_net_static_favicon_apng" {
  bucket = "eu.short.s3.ktls.aws.intellij.net"
  content_type = "image/apng"
  etag = filemd5("{root}/kotless/shortener/src/main/resources/favicon.apng")
  key = "static/favicon.apng"
  source = "{root}/kotless/shortener/src/main/resources/favicon.apng"
}

resource "aws_s3_bucket_object" "eu_short_s3_ktls_aws_intellij_net_static_js_shortener_js" {
  bucket = "eu.short.s3.ktls.aws.intellij.net"
  content_type = "application/javascript"
  etag = filemd5("{root}/kotless/shortener/src/main/resources/js/shortener.js")
  key = "static/js/shortener.js"
  source = "{root}/kotless/shortener/src/main/resources/js/shortener.js"
}

resource "aws_s3_bucket_object" "merged_0" {
  bucket = "eu.short.s3.ktls.aws.intellij.net"
  etag = filemd5("{root}/build/shortener/libs/shortener-0.1.7-beta-4-all.jar")
  key = "kotless-lambdas/short-merged-0.jar"
  source = "{root}/build/shortener/libs/shortener-0.1.7-beta-4-all.jar"
}

data "aws_acm_certificate" "short_kotless_io" {
  provider = aws.us_east_1
  domain = "short.kotless.io"
  statuses = ["ISSUED"]
}

data "aws_caller_identity" "current" {
  
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

data "aws_iam_policy_document" "merged_0" {
  statement {
    actions = ["dynamodb:BatchGetItem", "dynamodb:BatchWriteItem", "dynamodb:Create*", "dynamodb:Delete*", "dynamodb:Describe*", "dynamodb:GetItem", "dynamodb:List*", "dynamodb:PutItem", "dynamodb:Query", "dynamodb:Restore*", "dynamodb:Scan", "dynamodb:TagResource", "dynamodb:TransactGetItems", "dynamodb:TransactWriteItems", "dynamodb:UntagResource", "dynamodb:Update*"]
    effect = "Allow"
    resources = ["arn:aws:dynamodb:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:table/short-url-table"]
  }
  statement {
    actions = ["dynamodb:Query", "dynamodb:Scan"]
    effect = "Allow"
    resources = ["arn:aws:dynamodb:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:table/short-url-table/index/*"]
  }
  statement {
    actions = ["logs:CreateLogGroup", "logs:CreateLogStream", "logs:DeleteLogGroup", "logs:DeleteLogStream", "logs:DeleteMetricFilter", "logs:DescribeLogGroups", "logs:DescribeLogStreams", "logs:DescribeMetricFilters", "logs:GetLogEvents", "logs:GetLogGroupFields", "logs:GetLogRecord", "logs:GetQueryResults", "logs:PutLogEvents", "logs:PutMetricFilter"]
    effect = "Allow"
    resources = ["arn:aws:logs:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:*"]
  }
}

data "aws_iam_policy_document" "merged_0_assume" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      identifiers = ["apigateway.amazonaws.com", "lambda.amazonaws.com"]
      type = "Service"
    }
  }
}

data "aws_region" "current" {
  
}

data "aws_route53_zone" "kotless_io" {
  name = "kotless.io"
  private_zone = false
}

data "aws_s3_bucket" "kotless_bucket" {
  bucket = "eu.short.s3.ktls.aws.intellij.net"
}

output "application_url" {
  value = "https://short.kotless.io"
}

terraform {
  required_version = "0.12.29"
  backend "s3" {
    bucket = "eu.short.s3.ktls.aws.intellij.net"
    key = "kotless-state/state.tfstate"
    profile = "kotless-jetbrains"
    region = "eu-west-1"
  }
}

