package io.kotless.engine.terraform.aws.apigateway

import io.kotless.engine.terraform.TfResource
import io.kotless.engine.terraform.synthesizer.TfGroup
import io.kotless.engine.terraform.utils.tf

/**
 * Terraform aws_api_gateway_resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_resource.html">aws_api_gateway_resource</a>
 */
class TfRestApiResource(tfName: String, restApi: TfRestApi, parentResource: TfRestApiResource? = null,
                        path: String) : TfResource("aws_api_gateway_resource", tfName) {
    val id = "$tfFullName.id"
    val path = "$tfFullName.path"

    override val resourceDef = """
        |    rest_api_id = ${tf(restApi.id)}
        |    parent_id = ${tf(parentResource?.id ?: restApi.root_resource_id)}
        |    path_part = "$path"
        """

    override val group = TfGroup.RestApi
}
