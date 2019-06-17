package io.kotless.engine.terraform.aws.apigateway

import io.kotless.engine.terraform.TfResource
import io.kotless.engine.terraform.synthesizer.TfGroup
import io.kotless.engine.terraform.utils.tf

/**
 * Terraform aws_api_gateway_deployment resource.
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/api_gateway_deployment.html">aws_api_gateway_deployment</a>
 */
class TfRestApiDeployment(tfName: String, restApi: TfRestApi, stage: String,
                          lambdaIntegrations: List<TfRestApiLambdaIntegration>,
                          s3Integrations: List<TfRestApiS3Integration>) : TfResource("aws_api_gateway_deployment", tfName) {
    val id = "$tfFullName.id"
    val stage_name = "$tfFullName.stage_name"
    val invoke_url = "$tfFullName.invoke_url"

    init {
        dependsOn.addAll(lambdaIntegrations)
        dependsOn.addAll(s3Integrations)
    }

    override val resourceDef = """
        |    rest_api_id = ${tf(restApi.id)}
        |    stage_name = "$stage"
        |
        |    variables {
        |        deployed_at = ${tf("timestamp()")}
        |    }
        |
        |    lifecycle {
        |        create_before_destroy = true
        |    }
        """

    override val group = TfGroup.RestApi
}
