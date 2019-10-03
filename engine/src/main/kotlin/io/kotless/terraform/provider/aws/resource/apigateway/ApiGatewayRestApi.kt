package io.kotless.terraform.provider.aws.resource.apigateway

import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

/**
 * Terraform aws_api_gateway_rest_api resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_rest_api.html">aws_api_gateway_rest_api</a>
 */
class ApiGatewayRestApi(id: String) : TFResource(id, "aws_api_gateway_rest_api") {
    val id by text(inner = true)
    val root_resource_id by text(inner = true)

    var name by text()
    var binary_media_types by textArray()
}

fun api_gateway_rest_api(id: String, configure: ApiGatewayRestApi.() -> Unit) = ApiGatewayRestApi(id).apply(configure)

fun TFFile.api_gateway_rest_api(id: String, configure: ApiGatewayRestApi.() -> Unit) {
    add(ApiGatewayRestApi(id).apply(configure))
}
