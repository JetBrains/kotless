package io.kotless.terraform.infra

import io.kotless.hcl.HCLEntity
import io.kotless.utils.withIndent

/** Declaration of Terraform output */
class TFOutput(val name: String, val value: String) : HCLEntity() {
    override fun render(): String = """
        |output "$name" {
        |${"value = \"${value}\"".withIndent()}
        |}
        """.trimMargin()
}
