package io.kotless.engine.terraform.aws.apigateway

import io.kotless.MimeType
import io.kotless.engine.terraform.TfResource

/**
 * Terraform aws_api_gateway_rest_api resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_rest_api.html">aws_api_gateway_rest_api</a>
 */
class TfRestApi(tfName: String, awsName: String) : TfResource("aws_api_gateway_rest_api", tfName) {
    val id = "$tfFullName.id"
    val root_resource_id = "$tfFullName.root_resource_id"

    override val resourceDef = """
        |    name = "$awsName"
        |    binary_media_types = [${MimeType.values().filter { it.isBinary }.joinToString { "\"${it.mimeText}\"" }}]
        """
    override val group = io.kotless.engine.terraform.synthesizer.TfGroup.RestApi
}
