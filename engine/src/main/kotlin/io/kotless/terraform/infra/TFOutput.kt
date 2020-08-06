package io.kotless.terraform.infra

import io.kotless.hcl.HCLEntity
import io.kotless.hcl.HCLNamed
import io.kotless.utils.withIndent

/** Declaration of Terraform output */
class TFOutput(override val hcl_name: String, val value: String) : HCLEntity(), HCLNamed {
    override val hcl_ref: String
        get() = "output.$hcl_name"

    override fun render(): String = """
        |output "$hcl_name" {
        |${"value = \"${value}\"".withIndent()}
        |}
        """.trimMargin()
}
