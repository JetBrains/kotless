package io.kotless.terraform.provider.aws.resource.apigateway

import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

/**
 * Terraform aws_api_gateway_method resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_method.html">aws_api_gateway_method</a>
 */
class ApiGatewayMethod(id: String) : TFResource(id, "aws_api_gateway_method") {
    var rest_api_id by text()
    var resource_id by text()
    var http_method by text()
    var authorization by text()
}

fun api_gateway_method(id: String, configure: ApiGatewayMethod.() -> Unit) = ApiGatewayMethod(id).apply(configure)

fun TFFile.api_gateway_method(id: String, configure: ApiGatewayMethod.() -> Unit) {
    add(ApiGatewayMethod(id).apply(configure))
}
