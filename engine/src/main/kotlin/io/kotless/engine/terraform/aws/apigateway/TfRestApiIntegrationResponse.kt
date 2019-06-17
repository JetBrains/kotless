package io.kotless.engine.terraform.aws.apigateway

import io.kotless.engine.terraform.TfResource
import io.kotless.engine.terraform.utils.TfFieldValue
import io.kotless.engine.terraform.utils.tf

/**
 * Terraform aws_api_gateway_integration_response resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_integration_response.html">aws_api_gateway_integration_response</a>
 */
class TfRestApiIntegrationResponse(tfName: String, restApi: TfRestApi, resource: TfFieldValue, integration: TfRestApiS3Integration,
                                   method: TfRestApiMethod, status_code: Int) : TfResource("aws_api_gateway_integration_response", tfName) {
    init {
        dependsOn += integration
    }

    override val resourceDef = """
        |    rest_api_id = ${tf(restApi.id)}
        |    resource_id = ${resource()}
        |    http_method = ${tf(method.http_method)}
        |    status_code = $status_code
        |    response_parameters = {
        |        "method.response.header.Content-Type" = "integration.response.header.Content-Type"
        |        "method.response.header.Content-Length" = "integration.response.header.Content-Length"
        |    }
        """

    override val group = io.kotless.engine.terraform.synthesizer.TfGroup.RestApi
}
