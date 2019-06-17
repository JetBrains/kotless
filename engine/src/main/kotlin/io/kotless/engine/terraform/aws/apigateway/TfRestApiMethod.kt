package io.kotless.engine.terraform.aws.apigateway

import io.kotless.HttpMethod
import io.kotless.engine.terraform.TfResource
import io.kotless.engine.terraform.utils.TfFieldValue
import io.kotless.engine.terraform.utils.tf

/**
 * Terraform aws_api_gateway_method resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_method.html">aws_api_gateway_method</a>
 */
class TfRestApiMethod(tfName: String, restApi: TfRestApi, resource: TfFieldValue,
                      httpMethod: HttpMethod) : TfResource("aws_api_gateway_method", tfName) {
    val http_method = "$tfFullName.http_method"

    override val resourceDef = """
        |    rest_api_id = ${tf(restApi.id)}
        |    resource_id = ${resource()}
        |    http_method = "${httpMethod.name}"
        |    authorization = "NONE"
        """

    override val group = io.kotless.engine.terraform.synthesizer.TfGroup.RestApi
}
