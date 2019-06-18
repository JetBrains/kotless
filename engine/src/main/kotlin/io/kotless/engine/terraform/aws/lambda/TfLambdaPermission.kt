package io.kotless.engine.terraform.aws.lambda

import io.kotless.engine.terraform.TfResource
import io.kotless.engine.terraform.aws.apigateway.TfRestApi
import io.kotless.engine.terraform.aws.apigateway.TfRestApiMethod
import io.kotless.engine.terraform.aws.cloudwatch.TfCloudwatchEventRule
import io.kotless.engine.terraform.aws.data.AwsInformation
import io.kotless.engine.terraform.synthesizer.TfGroup
import io.kotless.engine.terraform.utils.*

/**
 * Terraform aws_lambda_permission resource configured for ApiGateway.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/lambda_permission.html">aws_lambda_permission</a>
 */
class TfLambdaApiGatewayPermission(tfName: String, lambda: TfLambda, restApi: TfRestApi, resource_path: TfFieldValue,
                                   method: TfRestApiMethod) : TfResource("aws_lambda_permission", tfName) {
    private val sourceArn = "arn:aws:execute-api:" +
            "${tfRaw(AwsInformation.tfRegion.name)}:" +
            "${tfRaw(AwsInformation.tfAccount.account_id)}:" +
            "${tfRaw(restApi.id)}/*/${tfRaw(method.http_method)}${resource_path()}"

    override val resourceDef = """
        |    statement_id = "${tfName.toAwsName()}"
        |    action = "lambda:InvokeFunction"
        |    function_name = ${tf(lambda.arn)}
        |    principal = "apigateway.amazonaws.com"
        |    source_arn = "$sourceArn"
        """

    override val group = TfGroup.Lambda
}

/**
 * Terraform aws_lambda_permission resource configured for CloudWatch.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/lambda_permission.html">aws_lambda_permission</a>
 */
class TfLambdaCloudwatchPermission(tfName: String, lambda: TfLambda, rule: TfCloudwatchEventRule) : TfResource("aws_lambda_permission", tfName) {
    override val resourceDef = """
        |    statement_id = "${tfName.toAwsName()}"
        |    action = "lambda:InvokeFunction"
        |    function_name = ${tf(lambda.arn)}
        |    principal = "events.amazonaws.com"
        |    source_arn = ${tf(rule.arn)}
        """

    override val group = TfGroup.Lambda
}
