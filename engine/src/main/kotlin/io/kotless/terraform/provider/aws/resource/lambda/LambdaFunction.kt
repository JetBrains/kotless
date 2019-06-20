package io.kotless.terraform.provider.aws.resource.lambda

import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

/**
 * Terraform aws_lambda_function resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/lambda_function.html">aws_lambda_function</a>
 */
class LambdaFunction(id: String) : TFResource(id, "aws_lambda_function") {
    var function_name by text()
    var role by text()

    var s3_bucket by text()
    var s3_key by text()

    var source_code_hash by text()

    var handler by text()
    var runtime by text()

    var timeout by int()
    var memory_size by int()
}

fun lambda_function(id: String, configure: LambdaFunction.() -> Unit) = LambdaFunction(id).apply(configure)

fun TFFile.lambda_function(id: String, configure: LambdaFunction.() -> Unit) {
    add(LambdaFunction(id).apply(configure))
}
