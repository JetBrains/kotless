package io.kotless.engine.terraform.aws.apigateway

import io.kotless.engine.terraform.TfResource
import io.kotless.engine.terraform.aws.data.AwsInformation
import io.kotless.engine.terraform.aws.iam.TfRole
import io.kotless.engine.terraform.aws.s3.TfS3Object
import io.kotless.engine.terraform.synthesizer.TfGroup
import io.kotless.engine.terraform.utils.*

/**
 * Terraform aws_api_gateway_integration resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/gateway_resource.html">aws_api_gateway_integration</a>
 * @see <a href="https://github.com/terraform-providers/terraform-provider-aws/pull/5667/files">Example of integration setup</a>
 */
class TfRestApiS3Integration(tfName: String, restApi: TfRestApi, resource: TfFieldValue,
                             method: TfRestApiMethod, staticS3Role: TfRole, s3Object: TfS3Object) : TfResource("aws_api_gateway_integration", tfName) {

    val methodResponse = TfRestApiMethodResponse(tfName.toTfName("200", "response"), restApi, resource, method, 200)
    val integrationResponse = TfRestApiIntegrationResponse(tfName.toTfName("200", "response"), restApi, resource, this, method, 200)

    override val resourceDef = """
        |    rest_api_id = ${tf(restApi.id)}
        |    resource_id = ${resource()}
        |    http_method = ${tf(method.http_method)}
        |    integration_http_method = "GET"
        |    type = "AWS"
        |    uri = "arn:aws:apigateway:${tfRaw(AwsInformation.tfRegion.name)}:s3:path/${tfRaw(s3Object.bucket)}/${tfRaw(s3Object.key)}"
        |    credentials = ${tf(staticS3Role.arn)}
        """

    override val group = TfGroup.RestApi
}
