package io.kotless.terraform.provider.aws.resource.lambda

import io.kotless.hcl.HCLEntity
import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

/**
 * Terraform aws_lambda_function resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/lambda_function.html">aws_lambda_function</a>
 */
class LambdaFunction(id: String) : TFResource(id, "aws_lambda_function") {
    val arn by text(inner = true)

    var function_name by text()

    /** ARN of role */
    var role by text()

    var s3_bucket by text()
    var s3_key by text()

    var source_code_hash by text()

    var handler by text()
    var runtime by text()

    var timeout by int()
    var memory_size by int()

    class Environment : HCLEntity.Inner("environment") {
        var variables by entity<HCLEntity>()
    }

    fun environment(configure: Environment.() -> Unit) {
        inner(Environment().apply(configure))
    }
}

fun lambda_function(id: String, configure: LambdaFunction.() -> Unit) = LambdaFunction(id).apply(configure)

fun TFFile.lambda_function(id: String, configure: LambdaFunction.() -> Unit) {
    add(LambdaFunction(id).apply(configure))
}
