package io.kotless.terraform.provider.aws.data.iam

import io.kotless.hcl.HCLEntity
import io.kotless.terraform.TFData
import io.kotless.terraform.TFFile


class IAMPolicyDocument(id: String) : TFData(id, "aws_iam_policy_document") {
    class Statement : HCLEntity() {
        var effect by text()
        var sid by text()

        class Principals : HCLEntity() {
            var identifiers by textArray()
            var type by text()
        }

        var principals by entity<Principals>()
        fun principals(configure: Principals.() -> Unit) {
            principals = Principals().apply(configure)
        }

        var resources by textArray()
        var actions by textArray()
    }

    val json by text(inner = true)

    var statement by entity<Statement>()
    fun statement(configure: Statement.() -> Unit) {
        statement = Statement().apply(configure)
    }
}

fun iam_policy_document(id: String, configure: IAMPolicyDocument.() -> Unit) = IAMPolicyDocument(id).apply(configure)

fun TFFile.iam_policy_document(id: String, configure: IAMPolicyDocument.() -> Unit) {
    add(IAMPolicyDocument(id).apply(configure))
}
