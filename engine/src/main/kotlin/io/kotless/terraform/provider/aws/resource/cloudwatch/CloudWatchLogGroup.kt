package io.kotless.terraform.provider.aws.resource.cloudwatch

import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

/**
 * Terraform aws_cloudwatch_log_group resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/aws_cloudwatch_log_group.html">aws_cloudwatch_log_group</a>
 */
class CloudWatchLogGroup(id: String) : TFResource(id, "aws_cloudwatch_log_group") {
    var name by text()
    var retention_in_days by int()
}

fun cloudwatch_log_group(id: String, configure: CloudWatchLogGroup.() -> Unit) = CloudWatchLogGroup(id).apply(configure)

fun TFFile.cloudwatch_log_group(id: String, configure: CloudWatchLogGroup.() -> Unit) {
    add(CloudWatchLogGroup(id).apply(configure))
}
