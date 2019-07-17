package io.kotless.terraform.provider.aws.resource.apigateway

import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

/**
 * Terraform aws_api_gateway_resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_resource.html">aws_api_gateway_resource</a>
 */
class ApiGatewayResource(id: String) : TFResource(id, "aws_api_gateway_resource") {
    val id by text(inner = true)
    val path by text(inner = true)

    var rest_api_id by text()
    var parent_id by text()
    var path_part by text()
}

fun api_gateway_resource(id: String, configure: ApiGatewayResource.() -> Unit) = ApiGatewayResource(id).apply(configure)

fun TFFile.api_gateway_resource(id: String, configure: ApiGatewayResource.() -> Unit) {
    add(ApiGatewayResource(id).apply(configure))
}
