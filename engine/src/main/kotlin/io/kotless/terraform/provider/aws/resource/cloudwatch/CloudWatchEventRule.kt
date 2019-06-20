package io.kotless.terraform.provider.aws.resource.cloudwatch

import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

/**
 * Terraform aws_cloudwatch_event_rule resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/cloudwatch_event_rule.html">aws_cloudwatch_event_rule</a>
 */
class CloudWatchEventRule(id: String) : TFResource(id, "aws_cloudwatch_event_rule") {
    val arn by text(inner = true)

    var name by text()
    var schedule_expression by text()
}

fun cloudwatch_event_rule(id: String, configure: CloudWatchEventRule.() -> Unit) = CloudWatchEventRule(id).apply(configure)

fun TFFile.cloudwatch_event_rule(id: String, configure: CloudWatchEventRule.() -> Unit) {
    add(CloudWatchEventRule(id).apply(configure))
}
