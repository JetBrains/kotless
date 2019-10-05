package io.kotless.terraform.provider.aws.resource.iam

import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

class IAMRole(id: String)  : TFResource(id, "aws_iam_role"){
    val name by text()
    val assume_role_policy by  text()
}

fun iam_role(id: String, configure: IAMRole.() -> Unit) = IAMRole(id).apply(configure)

fun TFFile.iam_role(id: String, configure: IAMRole.() -> Unit) {
    add(IAMRole(id).apply(configure))
}
