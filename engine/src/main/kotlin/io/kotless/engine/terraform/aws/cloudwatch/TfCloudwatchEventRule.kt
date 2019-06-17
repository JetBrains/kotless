package io.kotless.engine.terraform.aws.cloudwatch

import io.kotless.engine.terraform.TfResource
import io.kotless.engine.terraform.synthesizer.TfGroup


/**
 * Terraform aws_cloudwatch_event_rule resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/cloudwatch_event_rule.html">aws_cloudwatch_event_rule</a>
 */
class TfCloudwatchEventRule(tfName: String, name: String, scheduleExpression: String) : TfResource("aws_cloudwatch_event_rule", tfName) {
    val arn = "$tfFullName.arn"
    val name = "$tfFullName.name"

    override val resourceDef = """
        |    name = "$name"
        |    schedule_expression = "$scheduleExpression"
        """

    override val group = TfGroup.Cloudwatch
}
