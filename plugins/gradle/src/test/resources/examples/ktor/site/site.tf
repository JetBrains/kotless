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

resource "aws_api_gateway_base_path_mapping" "site" {
  api_id = aws_api_gateway_rest_api.site.id
  stage_name = aws_api_gateway_deployment.ktor_site.stage_name
  domain_name = "ktor.site.kotless.io"
}

resource "aws_api_gateway_deployment" "ktor_site" {
  depends_on = [aws_api_gateway_integration.css_highlight_style_css, aws_api_gateway_integration.css_kotless_site_css, aws_api_gateway_integration.favicon_apng, aws_api_gateway_integration.get, aws_api_gateway_integration.js_highlight_pack_js, aws_api_gateway_integration.pages_dsl_events_get, aws_api_gateway_integration.pages_dsl_http_get, aws_api_gateway_integration.pages_dsl_lifecycle_get, aws_api_gateway_integration.pages_dsl_overview_get, aws_api_gateway_integration.pages_dsl_permissions_get, aws_api_gateway_integration.pages_faq_get, aws_api_gateway_integration.pages_introduction_get, aws_api_gateway_integration.pages_plugin_configuration_get, aws_api_gateway_integration.pages_plugin_extensions_get, aws_api_gateway_integration.pages_plugin_overview_get, aws_api_gateway_integration.pages_plugin_tasks_get]
  rest_api_id = aws_api_gateway_rest_api.site.id
  stage_name = "1"
  variables = {
    deployed_at = timestamp()
  }
  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_api_gateway_domain_name" "site" {
  domain_name = "ktor.site.kotless.io"
  certificate_arn = data.aws_acm_certificate.ktor_site_kotless_io.arn
}

resource "aws_api_gateway_integration" "css_highlight_style_css" {
  depends_on = [aws_api_gateway_resource.css_highlight_style_css]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.css_highlight_style_css.id
  http_method = "GET"
  integration_http_method = "GET"
  type = "AWS"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:s3:path/${aws_s3_bucket_object.eu_ktor_site_s3_ktls_aws_intellij_net_static_css_highlight_style_css.bucket}/${aws_s3_bucket_object.eu_ktor_site_s3_ktls_aws_intellij_net_static_css_highlight_style_css.key}"
  credentials = aws_iam_role.kotless_static_role.arn
}

resource "aws_api_gateway_integration" "css_kotless_site_css" {
  depends_on = [aws_api_gateway_resource.css_kotless_site_css]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.css_kotless_site_css.id
  http_method = "GET"
  integration_http_method = "GET"
  type = "AWS"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:s3:path/${aws_s3_bucket_object.eu_ktor_site_s3_ktls_aws_intellij_net_static_css_kotless_site_css.bucket}/${aws_s3_bucket_object.eu_ktor_site_s3_ktls_aws_intellij_net_static_css_kotless_site_css.key}"
  credentials = aws_iam_role.kotless_static_role.arn
}

resource "aws_api_gateway_integration" "favicon_apng" {
  depends_on = [aws_api_gateway_resource.favicon_apng]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.favicon_apng.id
  http_method = "GET"
  integration_http_method = "GET"
  type = "AWS"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:s3:path/${aws_s3_bucket_object.eu_ktor_site_s3_ktls_aws_intellij_net_static_favicon_apng.bucket}/${aws_s3_bucket_object.eu_ktor_site_s3_ktls_aws_intellij_net_static_favicon_apng.key}"
  credentials = aws_iam_role.kotless_static_role.arn
}

resource "aws_api_gateway_integration" "get" {
  depends_on = [aws_api_gateway_rest_api.site]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_rest_api.site.root_resource_id
  http_method = "GET"
  integration_http_method = "POST"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "js_highlight_pack_js" {
  depends_on = [aws_api_gateway_resource.js_highlight_pack_js]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.js_highlight_pack_js.id
  http_method = "GET"
  integration_http_method = "GET"
  type = "AWS"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:s3:path/${aws_s3_bucket_object.eu_ktor_site_s3_ktls_aws_intellij_net_static_js_highlight_pack_js.bucket}/${aws_s3_bucket_object.eu_ktor_site_s3_ktls_aws_intellij_net_static_js_highlight_pack_js.key}"
  credentials = aws_iam_role.kotless_static_role.arn
}

resource "aws_api_gateway_integration" "pages_dsl_events_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_events]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_dsl_events.id
  http_method = "GET"
  integration_http_method = "POST"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_dsl_http_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_http]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_dsl_http.id
  http_method = "GET"
  integration_http_method = "POST"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_dsl_lifecycle_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_lifecycle]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_dsl_lifecycle.id
  http_method = "GET"
  integration_http_method = "POST"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_dsl_overview_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_overview]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_dsl_overview.id
  http_method = "GET"
  integration_http_method = "POST"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_dsl_permissions_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_permissions]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_dsl_permissions.id
  http_method = "GET"
  integration_http_method = "POST"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_faq_get" {
  depends_on = [aws_api_gateway_resource.pages_faq]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_faq.id
  http_method = "GET"
  integration_http_method = "POST"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_introduction_get" {
  depends_on = [aws_api_gateway_resource.pages_introduction]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_introduction.id
  http_method = "GET"
  integration_http_method = "POST"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_plugin_configuration_get" {
  depends_on = [aws_api_gateway_resource.pages_plugin_configuration]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_plugin_configuration.id
  http_method = "GET"
  integration_http_method = "POST"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_plugin_extensions_get" {
  depends_on = [aws_api_gateway_resource.pages_plugin_extensions]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_plugin_extensions.id
  http_method = "GET"
  integration_http_method = "POST"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_plugin_overview_get" {
  depends_on = [aws_api_gateway_resource.pages_plugin_overview]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_plugin_overview.id
  http_method = "GET"
  integration_http_method = "POST"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_plugin_tasks_get" {
  depends_on = [aws_api_gateway_resource.pages_plugin_tasks]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_plugin_tasks.id
  http_method = "GET"
  integration_http_method = "POST"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.merged_0.arn}/invocations"
}

resource "aws_api_gateway_integration_response" "css_highlight_style_css" {
  depends_on = [aws_api_gateway_integration.css_highlight_style_css, aws_api_gateway_method_response.css_highlight_style_css]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.css_highlight_style_css.id
  http_method = aws_api_gateway_method.css_highlight_style_css.http_method
  status_code = 200
  response_parameters = {
    "method.response.header.Content-Type" = "integration.response.header.Content-Type"
    "method.response.header.Content-Length" = "integration.response.header.Content-Length"
  }
}

resource "aws_api_gateway_integration_response" "css_kotless_site_css" {
  depends_on = [aws_api_gateway_integration.css_kotless_site_css, aws_api_gateway_method_response.css_kotless_site_css]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.css_kotless_site_css.id
  http_method = aws_api_gateway_method.css_kotless_site_css.http_method
  status_code = 200
  response_parameters = {
    "method.response.header.Content-Type" = "integration.response.header.Content-Type"
    "method.response.header.Content-Length" = "integration.response.header.Content-Length"
  }
}

resource "aws_api_gateway_integration_response" "favicon_apng" {
  depends_on = [aws_api_gateway_integration.favicon_apng, aws_api_gateway_method_response.favicon_apng]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.favicon_apng.id
  http_method = aws_api_gateway_method.favicon_apng.http_method
  status_code = 200
  response_parameters = {
    "method.response.header.Content-Type" = "integration.response.header.Content-Type"
    "method.response.header.Content-Length" = "integration.response.header.Content-Length"
  }
}

resource "aws_api_gateway_integration_response" "js_highlight_pack_js" {
  depends_on = [aws_api_gateway_integration.js_highlight_pack_js, aws_api_gateway_method_response.js_highlight_pack_js]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.js_highlight_pack_js.id
  http_method = aws_api_gateway_method.js_highlight_pack_js.http_method
  status_code = 200
  response_parameters = {
    "method.response.header.Content-Type" = "integration.response.header.Content-Type"
    "method.response.header.Content-Length" = "integration.response.header.Content-Length"
  }
}

resource "aws_api_gateway_method" "css_highlight_style_css" {
  depends_on = [aws_api_gateway_resource.css_highlight_style_css]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.css_highlight_style_css.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "css_kotless_site_css" {
  depends_on = [aws_api_gateway_resource.css_kotless_site_css]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.css_kotless_site_css.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "favicon_apng" {
  depends_on = [aws_api_gateway_resource.favicon_apng]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.favicon_apng.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "get" {
  depends_on = [aws_api_gateway_rest_api.site]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_rest_api.site.root_resource_id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "js_highlight_pack_js" {
  depends_on = [aws_api_gateway_resource.js_highlight_pack_js]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.js_highlight_pack_js.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "pages_dsl_events_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_events]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_dsl_events.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "pages_dsl_http_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_http]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_dsl_http.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "pages_dsl_lifecycle_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_lifecycle]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_dsl_lifecycle.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "pages_dsl_overview_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_overview]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_dsl_overview.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "pages_dsl_permissions_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_permissions]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_dsl_permissions.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "pages_faq_get" {
  depends_on = [aws_api_gateway_resource.pages_faq]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_faq.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "pages_introduction_get" {
  depends_on = [aws_api_gateway_resource.pages_introduction]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_introduction.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "pages_plugin_configuration_get" {
  depends_on = [aws_api_gateway_resource.pages_plugin_configuration]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_plugin_configuration.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "pages_plugin_extensions_get" {
  depends_on = [aws_api_gateway_resource.pages_plugin_extensions]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_plugin_extensions.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "pages_plugin_overview_get" {
  depends_on = [aws_api_gateway_resource.pages_plugin_overview]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_plugin_overview.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method" "pages_plugin_tasks_get" {
  depends_on = [aws_api_gateway_resource.pages_plugin_tasks]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.pages_plugin_tasks.id
  http_method = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_method_response" "css_highlight_style_css" {
  depends_on = [aws_api_gateway_method.css_highlight_style_css]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.css_highlight_style_css.id
  http_method = aws_api_gateway_method.css_highlight_style_css.http_method
  status_code = 200
  response_parameters = {
    "method.response.header.Content-Type" = true
    "method.response.header.Content-Length" = true
  }
}

resource "aws_api_gateway_method_response" "css_kotless_site_css" {
  depends_on = [aws_api_gateway_method.css_kotless_site_css]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.css_kotless_site_css.id
  http_method = aws_api_gateway_method.css_kotless_site_css.http_method
  status_code = 200
  response_parameters = {
    "method.response.header.Content-Type" = true
    "method.response.header.Content-Length" = true
  }
}

resource "aws_api_gateway_method_response" "favicon_apng" {
  depends_on = [aws_api_gateway_method.favicon_apng]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.favicon_apng.id
  http_method = aws_api_gateway_method.favicon_apng.http_method
  status_code = 200
  response_parameters = {
    "method.response.header.Content-Type" = true
    "method.response.header.Content-Length" = true
  }
}

resource "aws_api_gateway_method_response" "js_highlight_pack_js" {
  depends_on = [aws_api_gateway_method.js_highlight_pack_js]
  rest_api_id = aws_api_gateway_rest_api.site.id
  resource_id = aws_api_gateway_resource.js_highlight_pack_js.id
  http_method = aws_api_gateway_method.js_highlight_pack_js.http_method
  status_code = 200
  response_parameters = {
    "method.response.header.Content-Type" = true
    "method.response.header.Content-Length" = true
  }
}

resource "aws_api_gateway_resource" "css" {
  depends_on = [aws_api_gateway_rest_api.site]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_rest_api.site.root_resource_id
  path_part = "css"
}

resource "aws_api_gateway_resource" "css_highlight_style_css" {
  depends_on = [aws_api_gateway_resource.css]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_resource.css.id
  path_part = "highlight-style.css"
}

resource "aws_api_gateway_resource" "css_kotless_site_css" {
  depends_on = [aws_api_gateway_resource.css]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_resource.css.id
  path_part = "kotless-site.css"
}

resource "aws_api_gateway_resource" "favicon_apng" {
  depends_on = [aws_api_gateway_rest_api.site]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_rest_api.site.root_resource_id
  path_part = "favicon.apng"
}

resource "aws_api_gateway_resource" "js" {
  depends_on = [aws_api_gateway_rest_api.site]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_rest_api.site.root_resource_id
  path_part = "js"
}

resource "aws_api_gateway_resource" "js_highlight_pack_js" {
  depends_on = [aws_api_gateway_resource.js]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_resource.js.id
  path_part = "highlight.pack.js"
}

resource "aws_api_gateway_resource" "pages" {
  depends_on = [aws_api_gateway_rest_api.site]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_rest_api.site.root_resource_id
  path_part = "pages"
}

resource "aws_api_gateway_resource" "pages_dsl" {
  depends_on = [aws_api_gateway_resource.pages]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_resource.pages.id
  path_part = "dsl"
}

resource "aws_api_gateway_resource" "pages_dsl_events" {
  depends_on = [aws_api_gateway_resource.pages_dsl]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_resource.pages_dsl.id
  path_part = "events"
}

resource "aws_api_gateway_resource" "pages_dsl_http" {
  depends_on = [aws_api_gateway_resource.pages_dsl]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_resource.pages_dsl.id
  path_part = "http"
}

resource "aws_api_gateway_resource" "pages_dsl_lifecycle" {
  depends_on = [aws_api_gateway_resource.pages_dsl]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_resource.pages_dsl.id
  path_part = "lifecycle"
}

resource "aws_api_gateway_resource" "pages_dsl_overview" {
  depends_on = [aws_api_gateway_resource.pages_dsl]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_resource.pages_dsl.id
  path_part = "overview"
}

resource "aws_api_gateway_resource" "pages_dsl_permissions" {
  depends_on = [aws_api_gateway_resource.pages_dsl]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_resource.pages_dsl.id
  path_part = "permissions"
}

resource "aws_api_gateway_resource" "pages_faq" {
  depends_on = [aws_api_gateway_resource.pages]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_resource.pages.id
  path_part = "faq"
}

resource "aws_api_gateway_resource" "pages_introduction" {
  depends_on = [aws_api_gateway_resource.pages]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_resource.pages.id
  path_part = "introduction"
}

resource "aws_api_gateway_resource" "pages_plugin" {
  depends_on = [aws_api_gateway_resource.pages]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_resource.pages.id
  path_part = "plugin"
}

resource "aws_api_gateway_resource" "pages_plugin_configuration" {
  depends_on = [aws_api_gateway_resource.pages_plugin]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_resource.pages_plugin.id
  path_part = "configuration"
}

resource "aws_api_gateway_resource" "pages_plugin_extensions" {
  depends_on = [aws_api_gateway_resource.pages_plugin]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_resource.pages_plugin.id
  path_part = "extensions"
}

resource "aws_api_gateway_resource" "pages_plugin_overview" {
  depends_on = [aws_api_gateway_resource.pages_plugin]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_resource.pages_plugin.id
  path_part = "overview"
}

resource "aws_api_gateway_resource" "pages_plugin_tasks" {
  depends_on = [aws_api_gateway_resource.pages_plugin]
  rest_api_id = aws_api_gateway_rest_api.site.id
  parent_id = aws_api_gateway_resource.pages_plugin.id
  path_part = "tasks"
}

resource "aws_api_gateway_rest_api" "site" {
  name = "ktor-site-site"
  binary_media_types = ["application/gzip", "application/zip", "font/ttf", "image/apng", "image/bmp", "image/gif", "image/jpeg", "image/png", "image/svg", "image/webp"]
}

resource "aws_cloudwatch_event_rule" "autowarm_merged_0" {
  name = "ktor-site-autowarm-merged-0"
  schedule_expression = "cron(0/5 * * * ? *)"
}

resource "aws_cloudwatch_event_target" "autowarm_merged_0" {
  rule = aws_cloudwatch_event_rule.autowarm_merged_0.name
  arn = aws_lambda_function.merged_0.arn
}

resource "aws_iam_role" "kotless_static_role" {
  name = "ktor-site-kotless-static-role"
  assume_role_policy = data.aws_iam_policy_document.kotless_static_assume.json
}

resource "aws_iam_role" "merged_0" {
  name = "ktor-site-merged-0"
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
  function_name = "ktor-site-merged-0"
  role = aws_iam_role.merged_0.arn
  s3_bucket = "eu.ktor-site.s3.ktls.aws.intellij.net"
  s3_key = "kotless-lambdas/ktor-site-merged-0.jar"
  source_code_hash = filesha256(aws_s3_bucket_object.merged_0.source)
  handler = "io.kotless.examples.Server::handleRequest"
  runtime = "provided"
  timeout = 300
  memory_size = 1024
  environment {
    variables = {
      KOTLESS_PACKAGES = "io.kotless.examples"
    }
  }
}

resource "aws_lambda_permission" "autowarm_merged_0" {
  statement_id = "ktor-site-autowarm-merged-0"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "events.amazonaws.com"
  source_arn = aws_cloudwatch_event_rule.autowarm_merged_0.arn
}

resource "aws_lambda_permission" "get" {
  statement_id = "ktor-site-get"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/"
}

resource "aws_lambda_permission" "pages_dsl_events_get" {
  statement_id = "ktor-site-pages-dsl-events-get"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/dsl/events"
}

resource "aws_lambda_permission" "pages_dsl_http_get" {
  statement_id = "ktor-site-pages-dsl-http-get"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/dsl/http"
}

resource "aws_lambda_permission" "pages_dsl_lifecycle_get" {
  statement_id = "ktor-site-pages-dsl-lifecycle-get"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/dsl/lifecycle"
}

resource "aws_lambda_permission" "pages_dsl_overview_get" {
  statement_id = "ktor-site-pages-dsl-overview-get"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/dsl/overview"
}

resource "aws_lambda_permission" "pages_dsl_permissions_get" {
  statement_id = "ktor-site-pages-dsl-permissions-get"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/dsl/permissions"
}

resource "aws_lambda_permission" "pages_faq_get" {
  statement_id = "ktor-site-pages-faq-get"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/faq"
}

resource "aws_lambda_permission" "pages_introduction_get" {
  statement_id = "ktor-site-pages-introduction-get"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/introduction"
}

resource "aws_lambda_permission" "pages_plugin_configuration_get" {
  statement_id = "ktor-site-pages-plugin-configuration-get"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/plugin/configuration"
}

resource "aws_lambda_permission" "pages_plugin_extensions_get" {
  statement_id = "ktor-site-pages-plugin-extensions-get"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/plugin/extensions"
}

resource "aws_lambda_permission" "pages_plugin_overview_get" {
  statement_id = "ktor-site-pages-plugin-overview-get"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/plugin/overview"
}

resource "aws_lambda_permission" "pages_plugin_tasks_get" {
  statement_id = "ktor-site-pages-plugin-tasks-get"
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.merged_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/plugin/tasks"
}

resource "aws_route53_record" "ktor_site_kotless_io" {
  zone_id = data.aws_route53_zone.kotless_io.zone_id
  name = "ktor.site"
  type = "A"
  alias {
    name = aws_api_gateway_domain_name.site.cloudfront_domain_name
    zone_id = aws_api_gateway_domain_name.site.cloudfront_zone_id
    evaluate_target_health = false
  }
}

resource "aws_s3_bucket_object" "eu_ktor_site_s3_ktls_aws_intellij_net_static_css_highlight_style_css" {
  bucket = "eu.ktor-site.s3.ktls.aws.intellij.net"
  key = "static/css/highlight-style.css"
  source = "{root}/ktor/site/src/main/resources/static/css/highlight-style.css"
  etag = filemd5("{root}/ktor/site/src/main/resources/static/css/highlight-style.css")
  content_type = "text/css"
}

resource "aws_s3_bucket_object" "eu_ktor_site_s3_ktls_aws_intellij_net_static_css_kotless_site_css" {
  bucket = "eu.ktor-site.s3.ktls.aws.intellij.net"
  key = "static/css/kotless-site.css"
  source = "{root}/ktor/site/src/main/resources/static/css/kotless-site.css"
  etag = filemd5("{root}/ktor/site/src/main/resources/static/css/kotless-site.css")
  content_type = "text/css"
}

resource "aws_s3_bucket_object" "eu_ktor_site_s3_ktls_aws_intellij_net_static_favicon_apng" {
  bucket = "eu.ktor-site.s3.ktls.aws.intellij.net"
  key = "static/favicon.apng"
  source = "{root}/ktor/site/src/main/resources/static/favicon.apng"
  etag = filemd5("{root}/ktor/site/src/main/resources/static/favicon.apng")
  content_type = "image/apng"
}

resource "aws_s3_bucket_object" "eu_ktor_site_s3_ktls_aws_intellij_net_static_js_highlight_pack_js" {
  bucket = "eu.ktor-site.s3.ktls.aws.intellij.net"
  key = "static/js/highlight.pack.js"
  source = "{root}/ktor/site/src/main/resources/static/js/highlight.pack.js"
  etag = filemd5("{root}/ktor/site/src/main/resources/static/js/highlight.pack.js")
  content_type = "application/javascript"
}

resource "aws_s3_bucket_object" "merged_0" {
  bucket = "eu.ktor-site.s3.ktls.aws.intellij.net"
  key = "kotless-lambdas/ktor-site-merged-0.jar"
  source = "{root}/build/site/distributions/site-0.1.7-beta-3.zip"
  etag = filemd5("{root}/build/site/distributions/site-0.1.7-beta-3.zip")
}

data "aws_acm_certificate" "ktor_site_kotless_io" {
  provider = aws.us_east_1
  domain = "ktor.site.kotless.io"
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
  bucket = "eu.ktor-site.s3.ktls.aws.intellij.net"
}

output "application_url" {
  value = "https://ktor.site.kotless.io"
}

terraform {
  required_version = "0.12.29"
  backend "s3" {
    bucket = "eu.ktor-site.s3.ktls.aws.intellij.net"
    key = "kotless-state/state.tfstate"
    profile = "kotless-jetbrains"
    region = "eu-west-1"
  }
}

