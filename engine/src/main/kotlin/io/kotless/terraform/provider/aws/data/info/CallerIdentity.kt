package io.kotless.terraform.provider.aws.data.info

import io.kotless.terraform.TFData
import io.kotless.terraform.TFFile

/**
 * Terraform aws_caller_identity data.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/d/caller_identity.html">aws_caller_identity</a>
 */
class CallerIdentity(id: String) : TFData(id, "aws_caller_identity") {
    val account_id by text(inner = true)
}

fun caller_identity(id: String, configure: CallerIdentity.() -> Unit) = CallerIdentity(id).apply(configure)

fun TFFile.caller_identity(id: String, configure: CallerIdentity.() -> Unit) {
    add(CallerIdentity(id).apply(configure))
}
