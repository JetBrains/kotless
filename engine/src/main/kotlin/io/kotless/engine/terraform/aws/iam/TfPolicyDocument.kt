package io.kotless.engine.terraform.aws.iam

import io.kotless.engine.terraform.TfData
import io.kotless.engine.terraform.synthesizer.TfGroup


/**
 * Terraform aws_iam_policy_document data.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/d/iam_policy_document.html">aws_iam_policy_document</a>
 */
class TfPolicyDocument(tfName: String, statements: Set<TfPolicyDocument.Statement>) : TfData("aws_iam_policy_document", tfName) {
    val json = "$tfFullName.json"

    data class Principals(val identifiers: List<String>, val type: String) {
        fun render() = """
        |        principals {
        |            identifiers = [${identifiers.joinToString { "\"$it\"" }}]
        |            type = "$type"
        |        }
        """
    }

    data class Statement(val effect: String, val sid: String? = null,
                         val principals: Set<TfPolicyDocument.Principals>? = null, val resources: Set<String>? = null,
                         val actions: Set<String>) {
        fun render() = """
        |    statement {
        |        effect = "$effect"
        |        ${sid?.let { "sid = \"$it\"" } ?: ""}
        |        ${principals?.joinToString(separator = "\n") { it.render() } ?: ""}
        |        ${resources?.let { "resources = [${it.joinToString { "\"$it\"" }}]" } ?: ""}
        |        actions = [${actions.joinToString { "\"$it\"" }}]
        |    }
        """
    }

    override val dataDef: String = """
        |    ${statements.joinToString(separator = "\n") { it.render() }}
        """

    override val group = TfGroup.IAM
}

