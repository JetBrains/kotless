package io.kotless.terraform.provider.aws.resource.apigateway.response

import io.kotless.hcl.HCLEntity
import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

/**
 * Terraform aws_api_gateway_method_response resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_method_response.html">aws_api_gateway_method_response</a>
 */
class ApiGatewayMethodResponse(id: String) : TFResource(id, "aws_api_gateway_method_response") {
    var rest_api_id by text()
    var resource_id by text()
    var http_method by text()
    var status_code by int()

    var response_parameters by entity<HCLEntity>()
}

fun api_gateway_method_response(id: String, configure: ApiGatewayMethodResponse.() -> Unit) = ApiGatewayMethodResponse(id).apply(configure)

fun TFFile.api_gateway_method_response(id: String, configure: ApiGatewayMethodResponse.() -> Unit) {
    add(ApiGatewayMethodResponse(id).apply(configure))
}
