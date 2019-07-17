package io.kotless.terraform.provider.aws.resource.apigateway

import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource



/**
 * Terraform aws_api_gateway_integration resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_integration.html">aws_api_gateway_integration</a>
 */
class ApiGatewayIntegration(id: String) : TFResource(id, "aws_api_gateway_integration") {
    var rest_api_id by text()
    var resource_id by text()
    var http_method by text()
    var integration_http_method by text()
    var type by text()
    var uri by text()
    var credentials by text() //should be optional
}

fun api_gateway_integration(id: String, configure: ApiGatewayIntegration.() -> Unit) = ApiGatewayIntegration(id).apply(configure)

fun TFFile.api_gateway_integration(id: String, configure: ApiGatewayIntegration.() -> Unit) {
    add(ApiGatewayIntegration(id).apply(configure))
}
