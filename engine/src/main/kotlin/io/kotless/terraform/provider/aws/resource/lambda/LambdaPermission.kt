package io.kotless.terraform.provider.aws.resource.lambda

import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

/**
 * Terraform aws_lambda_permission resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/lambda_permission.html">aws_lambda_permission</a>
 */
class LambdaPermission(id: String) : TFResource(id, "aws_lambda_permission") {
    var statement_id by text()
    var action by text()
    var function_name by text()
    var principal by text()
    var source_arn by text()
}

fun lambda_permission(id: String, configure: LambdaPermission.() -> Unit) = LambdaPermission(id).apply(configure)

fun TFFile.lambda_permission(id: String, configure: LambdaPermission.() -> Unit) {
    add(LambdaPermission(id).apply(configure))
}
