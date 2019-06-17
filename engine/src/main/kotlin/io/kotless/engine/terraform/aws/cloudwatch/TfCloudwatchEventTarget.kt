package io.kotless.engine.terraform.aws.cloudwatch

import io.kotless.engine.terraform.TfResource
import io.kotless.engine.terraform.aws.lambda.TfLambda
import io.kotless.engine.terraform.aws.lambda.TfLambdaCloudwatchPermission
import io.kotless.engine.terraform.synthesizer.TfGroup
import io.kotless.engine.terraform.utils.tf
import io.kotless.engine.terraform.utils.toTfName

/**
 * Terraform aws_cloudwatch_event_target resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/cloudwatch_event_target.html">aws_cloudwatch_event_target</a>
 */
class TfCloudwatchEventTarget(tfName: String, lambda: TfLambda, rule: TfCloudwatchEventRule) : TfResource("aws_cloudwatch_event_target", tfName) {
    val arn = "$tfFullName.arn"

    val permission = TfLambdaCloudwatchPermission(tfName.toTfName("permission"), lambda, rule)

    override val resourceDef = """
        |    rule = ${tf(rule.name)}
        |    arn = ${tf(lambda.arn)}
        """

    override val group = TfGroup.Cloudwatch
}
