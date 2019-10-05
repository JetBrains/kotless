package io.kotless.terraform.provider.aws.resource.iam

import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

class IAMRolePolicy(id: String) : TFResource(id, "aws_iam_role_policy") {
    var role by text()
    var policy by text()
}

fun iam_role_policy(id: String, configure: IAMRolePolicy.() -> Unit) = IAMRolePolicy(id).apply(configure)

fun TFFile.iam_role_policy(id: String, configure: IAMRolePolicy.() -> Unit) {
    add(IAMRolePolicy(id).apply(configure))
}

