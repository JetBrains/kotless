package io.kotless.engine.terraform.aws.lambda

import io.kotless.engine.terraform.TfResource
import io.kotless.engine.terraform.aws.iam.TfRole
import io.kotless.engine.terraform.aws.s3.TfS3Object
import io.kotless.engine.terraform.synthesizer.TfGroup
import io.kotless.engine.terraform.utils.tf

/**
 * Terraform aws_lambda_function resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/lambda_function.html">aws_lambda_function</a>
 */
class TfLambda(tfName: String, val awsName: String, runtime: TfLambda.LambdaRuntime,
               s3Object: TfS3Object, role: TfRole) : TfResource("aws_lambda_function", tfName) {

    val arn = "$tfFullName.arn"

    data class LambdaRuntime(val handler: String, val timeout: Int, val memory: Int, val environment: Map<String, String> = emptyMap())

    override val resourceDef = """
        |    function_name = "$awsName"

        |    role = ${tf(role.arn)}

        |    s3_bucket = ${tf(s3Object.bucket)}
        |    s3_key = ${tf(s3Object.key)}
        |    source_code_hash = ${tf("base64sha256(file(${s3Object.source}))")}

        |    handler = "${runtime.handler}"
        |    runtime = "java8"
        |    timeout = "${runtime.timeout}"
        |    memory_size = "${runtime.memory}"

        |    ${if (runtime.environment.isNotEmpty()) {
        "environment = {\n|        variables = {\n${runtime.environment.map { "|            ${it.key} = \"${it.value}\"" }.joinToString(separator = "\n")}\n        }\n|    }"
    } else ""}
    """

    override val group = TfGroup.Lambda
}
