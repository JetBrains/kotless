package io.kotless.engine.terraform.aws.iam

import io.kotless.engine.terraform.TfResource
import io.kotless.engine.terraform.synthesizer.TfGroup
import io.kotless.engine.terraform.utils.tf

/**
 * Terraform aws_iam_policy resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/iam_policy.html">aws_iam_policy</a>
 */
class TfPolicy(tfName: String, awsName: String? = null, document: TfPolicyDocument) : TfResource("aws_iam_policy", tfName) {
    val arn = "$tfFullName.arn"
    val name = "$tfFullName.name"

    override val resourceDef = """
         |    ${awsName?.let { "name = \"$it\"" } ?: ""}
         |    policy = ${tf(document.json)}
        """

    override val group = TfGroup.IAM
}

/**
 * Terraform aws_iam_role_policy resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/iam_role_policy.html">aws_iam_role_policy</a>
 */
class TfRolePolicy(tfName: String, role: TfRole, awsName: String? = null, document: TfPolicyDocument) : TfResource("aws_iam_role_policy", tfName) {
    override val resourceDef = """
         |    ${awsName?.let { "name = \"$it\"" } ?: ""}
         |    role = ${tf(role.name)}
         |    policy = ${tf(document.json)}
        """

    override val group = TfGroup.IAM
}

/**
 * Terraform aws_iam_role_policy_attachment resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/iam_role_policy_attachment.html">aws_iam_role_policy_attachment</a>
 */
class TfPolicyAttachment(tfName: String, role: TfRole, policy: TfPolicy) : TfResource("aws_iam_role_policy_attachment", tfName) {
    init {
        dependsOn.addAll(listOf(policy, role))
    }

    override val resourceDef = """
         |    role = ${tf(role.name)}
         |    policy_arn = ${tf(policy.arn)}
        """

    override val group = TfGroup.IAM
}
