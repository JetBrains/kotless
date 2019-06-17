package io.kotless.engine.terraform.aws.apigateway

import io.kotless.engine.terraform.TfResource
import io.kotless.engine.terraform.synthesizer.TfGroup
import io.kotless.engine.terraform.utils.TfFieldValue
import io.kotless.engine.terraform.utils.tf

/**
 * Terraform aws_api_gateway_method_response resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_method_response.html">aws_api_gateway_method_response</a>
 */
class TfRestApiMethodResponse(tfName: String, restApi: TfRestApi, resource: TfFieldValue,
                              method: TfRestApiMethod, status_code: Int) : TfResource("aws_api_gateway_method_response", tfName) {

    override val resourceDef = """
        |    rest_api_id = ${tf(restApi.id)}
        |    resource_id = ${resource()}
        |    http_method = ${tf(method.http_method)}
        |    status_code = "$status_code"
        |    response_parameters = {
        |        "method.response.header.Content-Type" = true
        |        "method.response.header.Content-Length" = true
        |    }
        """

    override val group = TfGroup.RestApi
}
