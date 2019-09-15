package io.kotless.engine.terraform.aws.iam

import io.kotless.engine.terraform.TfResource
import io.kotless.engine.terraform.synthesizer.TfGroup
import io.kotless.engine.terraform.utils.tf

/** Assume policies for different AWS resources */
enum class TfRoleAssumePolicy {
    LambdaAssumePolicy {
        override fun toPolicyJson() =
            TfPolicyDocument("lambda_assume_policy",
                setOf(TfPolicyDocument.Statement("Allow", "AssumeRole",
                    setOf(
                        TfPolicyDocument.Principals(listOf("lambda.amazonaws.com", "apigateway.amazonaws.com"), "Service")
                    ),
                    actions = setOf("sts:AssumeRole")
                ))
            )

    },
    ApiGatewayAssumePolicy {
        override fun toPolicyJson() = TfPolicyDocument("apigateway_assume_policy",
            setOf(TfPolicyDocument.Statement("Allow", "AssumeRole",
                setOf(
                    TfPolicyDocument.Principals(listOf("apigateway.amazonaws.com"), "Service")
                ),
                actions = setOf("sts:AssumeRole")
            ))
        )
    };

    abstract fun toPolicyJson(): TfPolicyDocument
}

/**
 * Terraform aws_iam_role resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/iam_role.html">aws_iam_role</a>
 */
class TfRole(tfName: String, awsName: String? = null, assumePolicy: TfPolicyDocument) : TfResource("aws_iam_role", tfName) {
    val arn = "$tfFullName.arn"
    val name = "$tfFullName.name"

    override val resourceDef = """
          |    ${awsName?.let { "name = \"$it\"" } ?: ""}
          |    assume_role_policy = ${tf(assumePolicy.json)}
        """

    override val group = TfGroup.IAM
}
