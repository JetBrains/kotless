provider "aws" {
  region = "eu-west-1"
  profile = "kotless-jetbrains"
  version = "2.70.0"
}

provider "aws" {
  alias = "us_east_1"
  region = "us-east-1"
  profile = "kotless-jetbrains"
  version = "2.70.0"
}

resource "aws_api_gateway_base_path_mapping" "shortener" {
  api_id = aws_api_gateway_rest_api.shortener.id
  stage_name = aws_api_gateway_deployment.ktor_shortener.stage_name
  domain_name = "ktor.short.kotless.io"
}

resource "aws_api_gateway_deployment" "ktor_shortener" {
  depends_on = [aws_api_gateway_integration.css_shortener_css, aws_api_gateway_integration.favicon_apng, aws_api_gateway_integration.get, aws_api_gateway_integration.js_shortener_js, aws_api_gateway_integration.r_get, aws_api_gateway_integration.shorten_get]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  stage_name = "1"
  variables = {
    deployed_at = timestamp()
  }
  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_api_gateway_domain_name" "shortener" {
  domain_name = "ktor.short.kotless.io"
  certificate_arn = data.aws_acm_certificate.ktor_short_kotless_io.arn
}

resource "aws_api_gateway_integration" "css_shortener_css" {
  depends_on = [aws_api_gateway_resource.css_shortener_css]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_resource.css_shortener_css.id
  http_method = "GET"
  integration_http_method = "GET"
  type = "AWS"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:s3:path/${aws_s3_bucket_object.eu_ktor_short_s3_ktls_aws_intellij_net_static_css_shortener_css.bucket}/${aws_s3_bucket_object.eu_ktor_short_s3_ktls_aws_intellij_net_static_css_shortener_css.key}"
  credentials = aws_iam_role.kotless_static_role.arn
}

resource "aws_api_gateway_integration" "favicon_apng" {
  depends_on = [aws_api_gateway_resource.favicon_apng]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_resource.favicon_apng.id
  http_method = "GET"
  integration_http_method = "GET"
  type = "AWS"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:s3:path/${aws_s3_bucket_object.eu_ktor_short_s3_ktls_aws_intellij_net_static_favicon_apng.bucket}/${aws_s3_bucket_object.eu_ktor_short_s3_ktls_aws_intellij_net_static_favicon_apng.key}"
  credentials = aws_iam_role.kotless_static_role.arn
}

resource "aws_api_gateway_integration" "get" {
  depends_on = [aws_api_gateway_rest_api.shortener]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_rest_api.shortener.root_resource_id
  http_method = "GET"
  integration_http_method = "POST"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "js_shortener_js" {
  depends_on = [aws_api_gateway_resource.js_shortener_js]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_resource.js_shortener_js.id
  http_method = "GET"
  integration_http_method = "GET"
  type = "AWS"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:s3:path/${aws_s3_bucket_object.eu_ktor_short_s3_ktls_aws_intellij_net_static_js_shortener_js.bucket}/${aws_s3_bucket_object.eu_ktor_short_s3_ktls_aws_intellij_net_static_js_shortener_js.key}"
  credentials = aws_iam_role.kotless_static_role.arn
}

resource "aws_api_gateway_integration" "r_get" {
  depends_on = [aws_api_gateway_resource.r]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_resource.r.id
  http_method = "GET"
  integration_http_method = "POST"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "shorten_get" {
  depends_on = [aws_api_gateway_resource.shorten]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_resource.shorten.id
  http_method = "GET"
  integration_http_method = "POST"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
}

resource "aws_api_gateway_integration_response" "css_shortener_css" {
  depends_on = [aws_api_gateway_integration.css_shortener_css, aws_api_gateway_method_response.css_shortener_css]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_resource.css_shortener_css.id
  http_method = aws_api_gateway_method.css_shortener_css.http_method
  status_code = 200
  response_parameters = {
    "method.response.header.Content-Type" = "integration.response.header.Content-Type"
    "method.response.header.Content-Length" = "integration.response.header.Content-Length"
  }
}

resource "aws_api_gateway_integration_response" "favicon_apng" {
  depends_on = [aws_api_gateway_integration.favicon_apng, aws_api_gateway_method_response.favicon_apng]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_resource.favicon_apng.id
  http_method = aws_api_gateway_method.favicon_apng.http_method
  status_code = 200
  response_parameters = {
    "method.response.header.Content-Type" = "integration.response.header.Content-Type"
    "method.response.header.Content-Length" = "integration.response.header.Content-Length"
  }
}

resource "aws_api_gateway_integration_response" "js_shortener_js" {
  depends_on = [aws_api_gateway_integration.js_shortener_js, aws_api_gateway_method_response.js_shortener_js]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_resource.js_shortener_js.id
  http_method = aws_api_gateway_method.js_shortener_js.http_method
  status_code = 200
  response_parameters = {
    "method.response.header.Content-Type" = "integration.response.header.Content-Type"
    "method.response.header.Content-Length" = "integration.response.header.Content-Length"
  }
}

resource "aws_api_gateway_method" "css_shortener_css" {
  depends_on = [aws_api_gateway_resource.css_shortener_css]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_resource.css_shortener_css.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "favicon_apng" {
  depends_on = [aws_api_gateway_resource.favicon_apng]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_resource.favicon_apng.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "get" {
  depends_on = [aws_api_gateway_rest_api.shortener]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_rest_api.shortener.root_resource_id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "js_shortener_js" {
  depends_on = [aws_api_gateway_resource.js_shortener_js]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_resource.js_shortener_js.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "r_get" {
  depends_on = [aws_api_gateway_resource.r]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_resource.r.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "shorten_get" {
  depends_on = [aws_api_gateway_resource.shorten]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_resource.shorten.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method_response" "css_shortener_css" {
  depends_on = [aws_api_gateway_method.css_shortener_css]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_resource.css_shortener_css.id
  http_method = aws_api_gateway_method.css_shortener_css.http_method
  status_code = 200
  response_parameters = {
    "method.response.header.Content-Type" = true
    "method.response.header.Content-Length" = true
  }
}

resource "aws_api_gateway_method_response" "favicon_apng" {
  depends_on = [aws_api_gateway_method.favicon_apng]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_resource.favicon_apng.id
  http_method = aws_api_gateway_method.favicon_apng.http_method
  status_code = 200
  response_parameters = {
    "method.response.header.Content-Type" = true
    "method.response.header.Content-Length" = true
  }
}

resource "aws_api_gateway_method_response" "js_shortener_js" {
  depends_on = [aws_api_gateway_method.js_shortener_js]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  resource_id = aws_api_gateway_resource.js_shortener_js.id
  http_method = aws_api_gateway_method.js_shortener_js.http_method
  status_code = 200
  response_parameters = {
    "method.response.header.Content-Type" = true
    "method.response.header.Content-Length" = true
  }
}

resource "aws_api_gateway_resource" "css" {
  depends_on = [aws_api_gateway_rest_api.shortener]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  parent_id = aws_api_gateway_rest_api.shortener.root_resource_id
  path_part = "css"
}

resource "aws_api_gateway_resource" "css_shortener_css" {
  depends_on = [aws_api_gateway_resource.css]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  parent_id = aws_api_gateway_resource.css.id
  path_part = "shortener.css"
}

resource "aws_api_gateway_resource" "favicon_apng" {
  depends_on = [aws_api_gateway_rest_api.shortener]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  parent_id = aws_api_gateway_rest_api.shortener.root_resource_id
  path_part = "favicon.apng"
}

resource "aws_api_gateway_resource" "js" {
  depends_on = [aws_api_gateway_rest_api.shortener]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  parent_id = aws_api_gateway_rest_api.shortener.root_resource_id
  path_part = "js"
}

resource "aws_api_gateway_resource" "js_shortener_js" {
  depends_on = [aws_api_gateway_resource.js]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  parent_id = aws_api_gateway_resource.js.id
  path_part = "shortener.js"
}

resource "aws_api_gateway_resource" "r" {
  depends_on = [aws_api_gateway_rest_api.shortener]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  parent_id = aws_api_gateway_rest_api.shortener.root_resource_id
  path_part = "r"
}

resource "aws_api_gateway_resource" "shorten" {
  depends_on = [aws_api_gateway_rest_api.shortener]
  rest_api_id = aws_api_gateway_rest_api.shortener.id
  parent_id = aws_api_gateway_rest_api.shortener.root_resource_id
  path_part = "shorten"
}

resource "aws_api_gateway_rest_api" "shortener" {
  name = "ktor-short-shortener"
  binary_media_types = ["application/gzip", "application/zip", "font/ttf", "image/apng", "image/bmp", "image/gif", "image/jpeg", "image/png", "image/svg", "image/webp"]
}

resource "aws_cloudwatch_event_rule" "autowarm_merged_0" {
  name = "ktor-short-autowarm-merged-0"
  schedule_expression = "cron(0/5 * * * ? *)"
}

resource "aws_cloudwatch_event_target" "autowarm_merged_0" {
  rule = aws_cloudwatch_event_rule.autowarm_merged_0.name
  arn = aws_lambda_function.merged_0.arn
}

resource "aws_iam_role" "kotless_static_role" {
  name = "ktor-short-kotless-static-role"
  assume_role_policy = data.aws_iam_policy_document.kotless_static_assume.json
}

resource "aws_iam_role" "merged_0" {
  name = "ktor-short-merged-0"
  assume_role_policy = data.aws_iam_policy_document.merged_0_assume.json
}

resource "aws_iam_role_policy" "kotless_static_policy" {
  role = aws_iam_role.kotless_static_role.name
  policy = data.aws_iam_policy_document.kotless_static_policy.json
}

resource "aws_iam_role_policy" "merged_0" {
  role = aws_iam_role.merged_0.name
  policy = data.aws_iam_policy_document.merged_0.json
}

resource "aws_lambda_function" "merged_0" {
  function_name = "ktor-short-merged-0"
  role = aws_iam_role.merged_0.arn
  s3_bucket = "eu.ktor-short.s3.ktls.aws.intellij.net"
  s3_key = "kotless-lambdas/ktor-short-merged-0.jar"
  source_code_hash = filesha256(aws_s3_bucket_object.merged_0.source)
  handler = "io.kotless.examples.Server::handleRequest"
  runtime = "java11"
  timeout = 300
  memory_size = 1024
  environment {
    variables = {
      KOTLESS_PACKAGES = "io.kotless.examples"
    }
  }
}

resource "aws_lambda_permission" "autowarm_merged_0" {
  statement_id = "ktor-short-autowarm-merged-0"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "events.amazonaws.com"
  source_arn = aws_cloudwatch_event_rule.autowarm_merged_0.arn
}

resource "aws_lambda_permission" "get" {
  statement_id = "ktor-short-get"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.shortener.id}/*/GET/"
}

resource "aws_lambda_permission" "r_get" {
  statement_id = "ktor-short-r-get"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.shortener.id}/*/GET/r"
}

resource "aws_lambda_permission" "shorten_get" {
  statement_id = "ktor-short-shorten-get"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.shortener.id}/*/GET/shorten"
}

resource "aws_route53_record" "ktor_short_kotless_io" {
  zone_id = data.aws_route53_zone.kotless_io.zone_id
  name = "ktor.short"
  type = "A"
  alias {
    name = aws_api_gateway_domain_name.shortener.cloudfront_domain_name
    zone_id = aws_api_gateway_domain_name.shortener.cloudfront_zone_id
    evaluate_target_health = false
  }
}

resource "aws_s3_bucket_object" "eu_ktor_short_s3_ktls_aws_intellij_net_static_css_shortener_css" {
  bucket = "eu.ktor-short.s3.ktls.aws.intellij.net"
  key = "static/css/shortener.css"
  source = "{root}/ktor/shortener/src/main/resources/static/css/shortener.css"
  etag = filemd5("{root}/ktor/shortener/src/main/resources/static/css/shortener.css")
  content_type = "text/css"
}

resource "aws_s3_bucket_object" "eu_ktor_short_s3_ktls_aws_intellij_net_static_favicon_apng" {
  bucket = "eu.ktor-short.s3.ktls.aws.intellij.net"
  key = "static/favicon.apng"
  source = "{root}/ktor/shortener/src/main/resources/static/favicon.apng"
  etag = filemd5("{root}/ktor/shortener/src/main/resources/static/favicon.apng")
  content_type = "image/apng"
}

resource "aws_s3_bucket_object" "eu_ktor_short_s3_ktls_aws_intellij_net_static_js_shortener_js" {
  bucket = "eu.ktor-short.s3.ktls.aws.intellij.net"
  key = "static/js/shortener.js"
  source = "{root}/ktor/shortener/src/main/resources/static/js/shortener.js"
  etag = filemd5("{root}/ktor/shortener/src/main/resources/static/js/shortener.js")
  content_type = "application/javascript"
}

resource "aws_s3_bucket_object" "merged_0" {
  bucket = "eu.ktor-short.s3.ktls.aws.intellij.net"
  key = "kotless-lambdas/ktor-short-merged-0.jar"
  source = "{root}/build/shortener/libs/shortener-0.1.7-beta-2-all.jar"
  etag = filemd5("{root}/build/shortener/libs/shortener-0.1.7-beta-2-all.jar")
}

data "aws_acm_certificate" "ktor_short_kotless_io" {
  provider = aws.us_east_1
  domain = "ktor.short.kotless.io"
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
    effect = "Allow"
    resources = ["${data.aws_s3_bucket.kotless_bucket.arn}/*"]
    actions = ["s3:GetObject"]
  }
}

data "aws_iam_policy_document" "merged_0" {
  statement {
    effect = "Allow"
    resources = ["arn:aws:dynamodb:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:table/ktor-short-url-table"]
    actions = ["dynamodb:BatchGetItem", "dynamodb:BatchWriteItem", "dynamodb:Create*", "dynamodb:Delete*", "dynamodb:Describe*", "dynamodb:GetItem", "dynamodb:List*", "dynamodb:PutItem", "dynamodb:Query", "dynamodb:Restore*", "dynamodb:Scan", "dynamodb:TagResource", "dynamodb:TransactGetItems", "dynamodb:TransactWriteItems", "dynamodb:UntagResource", "dynamodb:Update*"]
  }
  statement {
    effect = "Allow"
    resources = ["arn:aws:logs:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:*"]
    actions = ["logs:CreateLogGroup", "logs:CreateLogStream", "logs:DeleteLogGroup", "logs:DeleteLogStream", "logs:DeleteMetricFilter", "logs:DescribeLogGroups", "logs:DescribeLogStreams", "logs:DescribeMetricFilters", "logs:GetLogEvents", "logs:GetLogGroupFields", "logs:GetLogRecord", "logs:GetQueryResults", "logs:PutLogEvents", "logs:PutMetricFilter"]
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
  bucket = "eu.ktor-short.s3.ktls.aws.intellij.net"
}

output "application_url" {
  value = "https://ktor.short.kotless.io"
}

terraform {
  required_version = "0.12.29"
  backend "s3" {
    bucket = "eu.ktor-short.s3.ktls.aws.intellij.net"
    key = "kotless-state/state.tfstate"
    profile = "kotless-jetbrains"
    region = "eu-west-1"
  }
}

