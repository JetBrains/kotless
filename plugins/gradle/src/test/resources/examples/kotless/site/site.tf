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

resource "aws_api_gateway_base_path_mapping" "site" {
  api_id = aws_api_gateway_rest_api.site.id
  domain_name = "site.kotless.io"
  stage_name = aws_api_gateway_deployment.kotless_site.stage_name
}

resource "aws_api_gateway_deployment" "kotless_site" {
  depends_on = [aws_api_gateway_integration.css_highlight_style_css, aws_api_gateway_integration.css_kotless_site_css, aws_api_gateway_integration.favicon_apng, aws_api_gateway_integration.get, aws_api_gateway_integration.js_highlight_pack_js, aws_api_gateway_integration.pages_dsl_events_get, aws_api_gateway_integration.pages_dsl_http_get, aws_api_gateway_integration.pages_dsl_lifecycle_get, aws_api_gateway_integration.pages_dsl_overview_get, aws_api_gateway_integration.pages_dsl_permissions_get, aws_api_gateway_integration.pages_faq_get, aws_api_gateway_integration.pages_introduction_get, aws_api_gateway_integration.pages_plugin_configuration_get, aws_api_gateway_integration.pages_plugin_extensions_get, aws_api_gateway_integration.pages_plugin_overview_get, aws_api_gateway_integration.pages_plugin_tasks_get]
  rest_api_id = aws_api_gateway_rest_api.site.id
  stage_name = "1"
  variables = {
    "deployed_at" = timestamp()
  }
  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_api_gateway_domain_name" "site" {
  certificate_arn = data.aws_acm_certificate.site_kotless_io.arn
  domain_name = "site.kotless.io"
}

resource "aws_api_gateway_integration" "css_highlight_style_css" {
  depends_on = [aws_api_gateway_resource.css_highlight_style_css]
  credentials = aws_iam_role.kotless_static_role.arn
  http_method = "GET"
  integration_http_method = "GET"
  resource_id = aws_api_gateway_resource.css_highlight_style_css.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  type = "AWS"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:s3:path/${aws_s3_bucket_object.eu_site_s3_ktls_aws_intellij_net_static_css_highlight_style_css.bucket}/${aws_s3_bucket_object.eu_site_s3_ktls_aws_intellij_net_static_css_highlight_style_css.key}"
}

resource "aws_api_gateway_integration" "css_kotless_site_css" {
  depends_on = [aws_api_gateway_resource.css_kotless_site_css]
  credentials = aws_iam_role.kotless_static_role.arn
  http_method = "GET"
  integration_http_method = "GET"
  resource_id = aws_api_gateway_resource.css_kotless_site_css.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  type = "AWS"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:s3:path/${aws_s3_bucket_object.eu_site_s3_ktls_aws_intellij_net_static_css_kotless_site_css.bucket}/${aws_s3_bucket_object.eu_site_s3_ktls_aws_intellij_net_static_css_kotless_site_css.key}"
}

resource "aws_api_gateway_integration" "favicon_apng" {
  depends_on = [aws_api_gateway_resource.favicon_apng]
  credentials = aws_iam_role.kotless_static_role.arn
  http_method = "GET"
  integration_http_method = "GET"
  resource_id = aws_api_gateway_resource.favicon_apng.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  type = "AWS"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:s3:path/${aws_s3_bucket_object.eu_site_s3_ktls_aws_intellij_net_static_favicon_apng.bucket}/${aws_s3_bucket_object.eu_site_s3_ktls_aws_intellij_net_static_favicon_apng.key}"
}

resource "aws_api_gateway_integration" "get" {
  depends_on = [aws_api_gateway_rest_api.site]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_rest_api.site.root_resource_id
  rest_api_id = aws_api_gateway_rest_api.site.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.page_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "js_highlight_pack_js" {
  depends_on = [aws_api_gateway_resource.js_highlight_pack_js]
  credentials = aws_iam_role.kotless_static_role.arn
  http_method = "GET"
  integration_http_method = "GET"
  resource_id = aws_api_gateway_resource.js_highlight_pack_js.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  type = "AWS"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:s3:path/${aws_s3_bucket_object.eu_site_s3_ktls_aws_intellij_net_static_js_highlight_pack_js.bucket}/${aws_s3_bucket_object.eu_site_s3_ktls_aws_intellij_net_static_js_highlight_pack_js.key}"
}

resource "aws_api_gateway_integration" "pages_dsl_events_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_events]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_resource.pages_dsl_events.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.page_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_dsl_http_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_http]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_resource.pages_dsl_http.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.page_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_dsl_lifecycle_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_lifecycle]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_resource.pages_dsl_lifecycle.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.page_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_dsl_overview_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_overview]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_resource.pages_dsl_overview.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.page_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_dsl_permissions_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_permissions]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_resource.pages_dsl_permissions.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.page_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_faq_get" {
  depends_on = [aws_api_gateway_resource.pages_faq]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_resource.pages_faq.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.page_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_introduction_get" {
  depends_on = [aws_api_gateway_resource.pages_introduction]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_resource.pages_introduction.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.page_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_plugin_configuration_get" {
  depends_on = [aws_api_gateway_resource.pages_plugin_configuration]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_resource.pages_plugin_configuration.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.page_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_plugin_extensions_get" {
  depends_on = [aws_api_gateway_resource.pages_plugin_extensions]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_resource.pages_plugin_extensions.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.page_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_plugin_overview_get" {
  depends_on = [aws_api_gateway_resource.pages_plugin_overview]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_resource.pages_plugin_overview.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.page_0.arn}/invocations"
}

resource "aws_api_gateway_integration" "pages_plugin_tasks_get" {
  depends_on = [aws_api_gateway_resource.pages_plugin_tasks]
  http_method = "GET"
  integration_http_method = "POST"
  resource_id = aws_api_gateway_resource.pages_plugin_tasks.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${data.aws_region.current.name}:lambda:path/2015-03-31/functions/${aws_lambda_function.page_0.arn}/invocations"
}

resource "aws_api_gateway_integration_response" "css_highlight_style_css" {
  depends_on = [aws_api_gateway_integration.css_highlight_style_css, aws_api_gateway_method_response.css_highlight_style_css]
  http_method = aws_api_gateway_method.css_highlight_style_css.http_method
  resource_id = aws_api_gateway_resource.css_highlight_style_css.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  status_code = "200"
  response_parameters = {
    "method.response.header.Content-Type" = "integration.response.header.Content-Type"
    "method.response.header.Content-Length" = "integration.response.header.Content-Length"
  }
}

resource "aws_api_gateway_integration_response" "css_kotless_site_css" {
  depends_on = [aws_api_gateway_integration.css_kotless_site_css, aws_api_gateway_method_response.css_kotless_site_css]
  http_method = aws_api_gateway_method.css_kotless_site_css.http_method
  resource_id = aws_api_gateway_resource.css_kotless_site_css.id
  rest_api_id = aws_api_gateway_rest_api.site.id
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
  rest_api_id = aws_api_gateway_rest_api.site.id
  status_code = "200"
  response_parameters = {
    "method.response.header.Content-Type" = "integration.response.header.Content-Type"
    "method.response.header.Content-Length" = "integration.response.header.Content-Length"
  }
}

resource "aws_api_gateway_integration_response" "js_highlight_pack_js" {
  depends_on = [aws_api_gateway_integration.js_highlight_pack_js, aws_api_gateway_method_response.js_highlight_pack_js]
  http_method = aws_api_gateway_method.js_highlight_pack_js.http_method
  resource_id = aws_api_gateway_resource.js_highlight_pack_js.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  status_code = "200"
  response_parameters = {
    "method.response.header.Content-Type" = "integration.response.header.Content-Type"
    "method.response.header.Content-Length" = "integration.response.header.Content-Length"
  }
}

resource "aws_api_gateway_method" "css_highlight_style_css" {
  depends_on = [aws_api_gateway_resource.css_highlight_style_css]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.css_highlight_style_css.id
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_method" "css_kotless_site_css" {
  depends_on = [aws_api_gateway_resource.css_kotless_site_css]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.css_kotless_site_css.id
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_method" "favicon_apng" {
  depends_on = [aws_api_gateway_resource.favicon_apng]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.favicon_apng.id
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_method" "get" {
  depends_on = [aws_api_gateway_rest_api.site]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_rest_api.site.root_resource_id
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_method" "js_highlight_pack_js" {
  depends_on = [aws_api_gateway_resource.js_highlight_pack_js]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.js_highlight_pack_js.id
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_method" "pages_dsl_events_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_events]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.pages_dsl_events.id
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_method" "pages_dsl_http_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_http]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.pages_dsl_http.id
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_method" "pages_dsl_lifecycle_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_lifecycle]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.pages_dsl_lifecycle.id
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_method" "pages_dsl_overview_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_overview]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.pages_dsl_overview.id
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_method" "pages_dsl_permissions_get" {
  depends_on = [aws_api_gateway_resource.pages_dsl_permissions]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.pages_dsl_permissions.id
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_method" "pages_faq_get" {
  depends_on = [aws_api_gateway_resource.pages_faq]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.pages_faq.id
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_method" "pages_introduction_get" {
  depends_on = [aws_api_gateway_resource.pages_introduction]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.pages_introduction.id
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_method" "pages_plugin_configuration_get" {
  depends_on = [aws_api_gateway_resource.pages_plugin_configuration]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.pages_plugin_configuration.id
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_method" "pages_plugin_extensions_get" {
  depends_on = [aws_api_gateway_resource.pages_plugin_extensions]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.pages_plugin_extensions.id
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_method" "pages_plugin_overview_get" {
  depends_on = [aws_api_gateway_resource.pages_plugin_overview]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.pages_plugin_overview.id
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_method" "pages_plugin_tasks_get" {
  depends_on = [aws_api_gateway_resource.pages_plugin_tasks]
  authorization = "NONE"
  http_method = "GET"
  resource_id = aws_api_gateway_resource.pages_plugin_tasks.id
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_method_response" "css_highlight_style_css" {
  depends_on = [aws_api_gateway_method.css_highlight_style_css]
  http_method = aws_api_gateway_method.css_highlight_style_css.http_method
  resource_id = aws_api_gateway_resource.css_highlight_style_css.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  status_code = "200"
  response_parameters = {
    "method.response.header.Content-Type" = true
    "method.response.header.Content-Length" = true
  }
}

resource "aws_api_gateway_method_response" "css_kotless_site_css" {
  depends_on = [aws_api_gateway_method.css_kotless_site_css]
  http_method = aws_api_gateway_method.css_kotless_site_css.http_method
  resource_id = aws_api_gateway_resource.css_kotless_site_css.id
  rest_api_id = aws_api_gateway_rest_api.site.id
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
  rest_api_id = aws_api_gateway_rest_api.site.id
  status_code = "200"
  response_parameters = {
    "method.response.header.Content-Type" = true
    "method.response.header.Content-Length" = true
  }
}

resource "aws_api_gateway_method_response" "js_highlight_pack_js" {
  depends_on = [aws_api_gateway_method.js_highlight_pack_js]
  http_method = aws_api_gateway_method.js_highlight_pack_js.http_method
  resource_id = aws_api_gateway_resource.js_highlight_pack_js.id
  rest_api_id = aws_api_gateway_rest_api.site.id
  status_code = "200"
  response_parameters = {
    "method.response.header.Content-Type" = true
    "method.response.header.Content-Length" = true
  }
}

resource "aws_api_gateway_resource" "css" {
  depends_on = [aws_api_gateway_rest_api.site]
  parent_id = aws_api_gateway_rest_api.site.root_resource_id
  path_part = "css"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "css_highlight_style_css" {
  depends_on = [aws_api_gateway_resource.css]
  parent_id = aws_api_gateway_resource.css.id
  path_part = "highlight-style.css"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "css_kotless_site_css" {
  depends_on = [aws_api_gateway_resource.css]
  parent_id = aws_api_gateway_resource.css.id
  path_part = "kotless-site.css"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "favicon_apng" {
  depends_on = [aws_api_gateway_rest_api.site]
  parent_id = aws_api_gateway_rest_api.site.root_resource_id
  path_part = "favicon.apng"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "js" {
  depends_on = [aws_api_gateway_rest_api.site]
  parent_id = aws_api_gateway_rest_api.site.root_resource_id
  path_part = "js"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "js_highlight_pack_js" {
  depends_on = [aws_api_gateway_resource.js]
  parent_id = aws_api_gateway_resource.js.id
  path_part = "highlight.pack.js"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "pages" {
  depends_on = [aws_api_gateway_rest_api.site]
  parent_id = aws_api_gateway_rest_api.site.root_resource_id
  path_part = "pages"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "pages_dsl" {
  depends_on = [aws_api_gateway_resource.pages]
  parent_id = aws_api_gateway_resource.pages.id
  path_part = "dsl"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "pages_dsl_events" {
  depends_on = [aws_api_gateway_resource.pages_dsl]
  parent_id = aws_api_gateway_resource.pages_dsl.id
  path_part = "events"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "pages_dsl_http" {
  depends_on = [aws_api_gateway_resource.pages_dsl]
  parent_id = aws_api_gateway_resource.pages_dsl.id
  path_part = "http"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "pages_dsl_lifecycle" {
  depends_on = [aws_api_gateway_resource.pages_dsl]
  parent_id = aws_api_gateway_resource.pages_dsl.id
  path_part = "lifecycle"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "pages_dsl_overview" {
  depends_on = [aws_api_gateway_resource.pages_dsl]
  parent_id = aws_api_gateway_resource.pages_dsl.id
  path_part = "overview"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "pages_dsl_permissions" {
  depends_on = [aws_api_gateway_resource.pages_dsl]
  parent_id = aws_api_gateway_resource.pages_dsl.id
  path_part = "permissions"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "pages_faq" {
  depends_on = [aws_api_gateway_resource.pages]
  parent_id = aws_api_gateway_resource.pages.id
  path_part = "faq"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "pages_introduction" {
  depends_on = [aws_api_gateway_resource.pages]
  parent_id = aws_api_gateway_resource.pages.id
  path_part = "introduction"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "pages_plugin" {
  depends_on = [aws_api_gateway_resource.pages]
  parent_id = aws_api_gateway_resource.pages.id
  path_part = "plugin"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "pages_plugin_configuration" {
  depends_on = [aws_api_gateway_resource.pages_plugin]
  parent_id = aws_api_gateway_resource.pages_plugin.id
  path_part = "configuration"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "pages_plugin_extensions" {
  depends_on = [aws_api_gateway_resource.pages_plugin]
  parent_id = aws_api_gateway_resource.pages_plugin.id
  path_part = "extensions"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "pages_plugin_overview" {
  depends_on = [aws_api_gateway_resource.pages_plugin]
  parent_id = aws_api_gateway_resource.pages_plugin.id
  path_part = "overview"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_resource" "pages_plugin_tasks" {
  depends_on = [aws_api_gateway_resource.pages_plugin]
  parent_id = aws_api_gateway_resource.pages_plugin.id
  path_part = "tasks"
  rest_api_id = aws_api_gateway_rest_api.site.id
}

resource "aws_api_gateway_rest_api" "site" {
  binary_media_types = ["application/epub+zip", "application/gzip", "application/java-archive", "application/msword", "application/pdf", "application/vnd.ms-excel", "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/zip", "audio/aac", "audio/mp4", "audio/mpeg", "audio/webm", "font/ttf", "font/woff", "font/woff2", "image/apng", "image/bmp", "image/gif", "image/heic", "image/jpeg", "image/png", "image/vnd.microsoft.icon", "image/webp", "video/mp4", "video/webm"]
  name = "site-site"
}

resource "aws_cloudwatch_event_rule" "autowarm_page_0" {
  name = "site-autowarm-page-0"
  schedule_expression = "cron(0/5 * * * ? *)"
}

resource "aws_cloudwatch_event_target" "autowarm_page_0" {
  arn = aws_lambda_function.page_0.arn
  rule = aws_cloudwatch_event_rule.autowarm_page_0.name
}

resource "aws_iam_role" "kotless_static_role" {
  assume_role_policy = data.aws_iam_policy_document.kotless_static_assume.json
  name = "site-kotless-static-role"
}

resource "aws_iam_role" "page_0" {
  assume_role_policy = data.aws_iam_policy_document.page_0_assume.json
  name = "site-page-0"
}

resource "aws_iam_role_policy" "kotless_static_policy" {
  policy = data.aws_iam_policy_document.kotless_static_policy.json
  role = aws_iam_role.kotless_static_role.name
}

resource "aws_iam_role_policy" "page_0" {
  policy = data.aws_iam_policy_document.page_0.json
  role = aws_iam_role.page_0.name
}

resource "aws_lambda_function" "page_0" {
  function_name = "site-page-0"
  handler = "io.kotless.dsl.LambdaHandler::handleRequest"
  memory_size = 1024
  role = aws_iam_role.page_0.arn
  runtime = "java11"
  s3_bucket = "eu.site.s3.ktls.aws.intellij.net"
  s3_key = "kotless-lambdas/site-page-0.jar"
  source_code_hash = filesha256(aws_s3_bucket_object.page_0.source)
  timeout = 300
  environment {
    variables = {
      "KOTLESS_PACKAGES" = "io.kotless.examples"
    }
  }
}

resource "aws_lambda_permission" "autowarm_page_0" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.page_0.arn
  principal = "events.amazonaws.com"
  source_arn = aws_cloudwatch_event_rule.autowarm_page_0.arn
  statement_id = "site-autowarm-page-0"
}

resource "aws_lambda_permission" "get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.page_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/"
  statement_id = "site-get"
}

resource "aws_lambda_permission" "pages_dsl_events_get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.page_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/dsl/events"
  statement_id = "site-pages-dsl-events-get"
}

resource "aws_lambda_permission" "pages_dsl_http_get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.page_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/dsl/http"
  statement_id = "site-pages-dsl-http-get"
}

resource "aws_lambda_permission" "pages_dsl_lifecycle_get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.page_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/dsl/lifecycle"
  statement_id = "site-pages-dsl-lifecycle-get"
}

resource "aws_lambda_permission" "pages_dsl_overview_get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.page_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/dsl/overview"
  statement_id = "site-pages-dsl-overview-get"
}

resource "aws_lambda_permission" "pages_dsl_permissions_get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.page_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/dsl/permissions"
  statement_id = "site-pages-dsl-permissions-get"
}

resource "aws_lambda_permission" "pages_faq_get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.page_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/faq"
  statement_id = "site-pages-faq-get"
}

resource "aws_lambda_permission" "pages_introduction_get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.page_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/introduction"
  statement_id = "site-pages-introduction-get"
}

resource "aws_lambda_permission" "pages_plugin_configuration_get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.page_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/plugin/configuration"
  statement_id = "site-pages-plugin-configuration-get"
}

resource "aws_lambda_permission" "pages_plugin_extensions_get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.page_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/plugin/extensions"
  statement_id = "site-pages-plugin-extensions-get"
}

resource "aws_lambda_permission" "pages_plugin_overview_get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.page_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/plugin/overview"
  statement_id = "site-pages-plugin-overview-get"
}

resource "aws_lambda_permission" "pages_plugin_tasks_get" {
  action = "lambda:InvokeFunction"
  function_name = aws_lambda_function.page_0.arn
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.site.id}/*/GET/pages/plugin/tasks"
  statement_id = "site-pages-plugin-tasks-get"
}

resource "aws_route53_record" "site_kotless_io" {
  name = "site"
  type = "A"
  zone_id = data.aws_route53_zone.kotless_io.zone_id
  alias {
    evaluate_target_health = false
    name = aws_api_gateway_domain_name.site.cloudfront_domain_name
    zone_id = aws_api_gateway_domain_name.site.cloudfront_zone_id
  }
}

resource "aws_s3_bucket_object" "eu_site_s3_ktls_aws_intellij_net_static_css_highlight_style_css" {
  bucket = "eu.site.s3.ktls.aws.intellij.net"
  content_type = "text/css"
  etag = filemd5("{root}/kotless/site/src/main/resources/css/highlight-style.css")
  key = "static/css/highlight-style.css"
  source = "{root}/kotless/site/src/main/resources/css/highlight-style.css"
}

resource "aws_s3_bucket_object" "eu_site_s3_ktls_aws_intellij_net_static_css_kotless_site_css" {
  bucket = "eu.site.s3.ktls.aws.intellij.net"
  content_type = "text/css"
  etag = filemd5("{root}/kotless/site/src/main/resources/css/kotless-site.css")
  key = "static/css/kotless-site.css"
  source = "{root}/kotless/site/src/main/resources/css/kotless-site.css"
}

resource "aws_s3_bucket_object" "eu_site_s3_ktls_aws_intellij_net_static_favicon_apng" {
  bucket = "eu.site.s3.ktls.aws.intellij.net"
  content_type = "image/apng"
  etag = filemd5("{root}/kotless/site/src/main/resources/favicon.apng")
  key = "static/favicon.apng"
  source = "{root}/kotless/site/src/main/resources/favicon.apng"
}

resource "aws_s3_bucket_object" "eu_site_s3_ktls_aws_intellij_net_static_js_highlight_pack_js" {
  bucket = "eu.site.s3.ktls.aws.intellij.net"
  content_type = "application/javascript"
  etag = filemd5("{root}/kotless/site/src/main/resources/js/highlight.pack.js")
  key = "static/js/highlight.pack.js"
  source = "{root}/kotless/site/src/main/resources/js/highlight.pack.js"
}

resource "aws_s3_bucket_object" "page_0" {
  bucket = "eu.site.s3.ktls.aws.intellij.net"
  etag = filemd5("{root}/build/site/libs/site-0.1.7-beta-5-all.jar")
  key = "kotless-lambdas/site-page-0.jar"
  source = "{root}/build/site/libs/site-0.1.7-beta-5-all.jar"
}

data "aws_acm_certificate" "site_kotless_io" {
  provider = aws.us_east_1
  domain = "site.kotless.io"
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

data "aws_iam_policy_document" "page_0" {
  statement {
    actions = ["logs:CreateLogGroup", "logs:CreateLogStream", "logs:DeleteLogGroup", "logs:DeleteLogStream", "logs:DeleteMetricFilter", "logs:DescribeLogGroups", "logs:DescribeLogStreams", "logs:DescribeMetricFilters", "logs:GetLogEvents", "logs:GetLogGroupFields", "logs:GetLogRecord", "logs:GetQueryResults", "logs:PutLogEvents", "logs:PutMetricFilter"]
    effect = "Allow"
    resources = ["arn:aws:logs:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:*"]
  }
}

data "aws_iam_policy_document" "page_0_assume" {
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
  bucket = "eu.site.s3.ktls.aws.intellij.net"
}

output "application_url" {
  value = "https://site.kotless.io"
}

terraform {
  required_version = "0.12.29"
  backend "s3" {
    bucket = "eu.site.s3.ktls.aws.intellij.net"
    key = "kotless-state/state.tfstate"
    profile = "kotless-jetbrains"
    region = "eu-west-1"
  }
}

