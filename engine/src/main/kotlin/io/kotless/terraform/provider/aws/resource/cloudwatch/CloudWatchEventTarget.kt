package io.kotless.terraform.provider.aws.resource.cloudwatch

import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

/**
 * Terraform aws_cloudwatch_event_target resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/cloudwatch_event_target.html">aws_cloudwatch_event_target</a>
 */
class CloudWatchEventTarget(id: String) : TFResource(id, "aws_cloudwatch_event_target") {
    var rule by text()
    var arn by text()
}

fun cloudwatch_event_target(id: String, configure: CloudWatchEventTarget.() -> Unit) = CloudWatchEventTarget(id).apply(configure)

fun TFFile.cloudwatch_event_target(id: String, configure: CloudWatchEventTarget.() -> Unit) {
    add(CloudWatchEventTarget(id).apply(configure))
}
