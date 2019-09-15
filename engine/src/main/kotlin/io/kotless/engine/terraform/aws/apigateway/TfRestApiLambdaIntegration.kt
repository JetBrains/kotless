package io.kotless.engine.terraform.aws.apigateway

import io.kotless.engine.terraform.TfResource
import io.kotless.engine.terraform.aws.data.AwsInformation
import io.kotless.engine.terraform.aws.lambda.TfLambda
import io.kotless.engine.terraform.aws.lambda.TfLambdaApiGatewayPermission
import io.kotless.engine.terraform.synthesizer.TfGroup
import io.kotless.engine.terraform.utils.*

/**
 * Terraform aws_api_gateway_integration resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_integration.html">aws_api_gateway_integration</a>
 */
class TfRestApiLambdaIntegration(tfName: String, restApi: TfRestApi, resource: TfFieldValue, resource_path: TfFieldValue,
                                 method: TfRestApiMethod, lambda: TfLambda) : TfResource("aws_api_gateway_integration", tfName) {

    val tfPermission = TfLambdaApiGatewayPermission(tfName.toTfName("lambda", "permission"), lambda,
        restApi, resource_path, method)

    override val resourceDef = """
        |    rest_api_id = ${tf(restApi.id)}
        |    resource_id = ${resource()}
        |    http_method = ${tf(method.http_method)}
        |    integration_http_method = "POST"
        |    type = "AWS_PROXY"
        |    uri = "arn:aws:apigateway:${tfRaw(AwsInformation.tfRegion.name)}:lambda:path/2015-03-31/functions/${tfRaw(lambda.arn)}/invocations"
        """

    override val group = TfGroup.RestApi
}
