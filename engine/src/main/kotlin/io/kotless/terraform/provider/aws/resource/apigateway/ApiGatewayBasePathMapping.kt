package io.kotless.terraform.provider.aws.resource.apigateway

import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

/**
 * Terraform aws_api_gateway_base_path_mapping resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_base_path_mapping.html">aws_api_gateway_base_path_mapping</a>
 */
class ApiGatewayBasePathMapping(id: String) : TFResource(id, "aws_api_gateway_base_path_mapping") {
    var api_id by text()
    var stage_name by text()
    var domain_name by text()
}

fun api_gateway_base_path_mapping(id: String, configure: ApiGatewayBasePathMapping.() -> Unit) = ApiGatewayBasePathMapping(id).apply(configure)

fun TFFile.api_gateway_base_path_mapping(id: String, configure: ApiGatewayBasePathMapping.() -> Unit) {
    add(ApiGatewayBasePathMapping(id).apply(configure))
}
