package io.kotless.terraform.provider.aws.resource.apigateway.response

import io.kotless.hcl.HCLEntity
import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource


/**
 * Terraform aws_api_gateway_integration_response resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_integration_response.html">aws_api_gateway_integration_response</a>
 */
class ApiGatewayIntegrationResponse(id: String) : TFResource(id, "aws_api_gateway_integration_response") {
    var rest_api_id by text()
    var resource_id by text()
    var http_method by text()
    var status_code by int()

    val response_parameters by entity<HCLEntity>(default = HCLEntity())
}

fun api_gateway_integration_response(id: String, configure: ApiGatewayIntegrationResponse.() -> Unit) = ApiGatewayIntegrationResponse(id).apply(configure)

fun TFFile.api_gateway_integration_response(id: String, configure: ApiGatewayIntegrationResponse.() -> Unit) {
    add(ApiGatewayIntegrationResponse(id).apply(configure))
}
