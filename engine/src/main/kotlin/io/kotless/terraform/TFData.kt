package io.kotless.terraform

import io.kotless.hcl.HCLEntity
import io.kotless.hcl.HCLNamed
import io.kotless.utils.withIndent

/** Representation of Terraform Data */
open class TFData(val tf_id: String, val tf_type: String) : HCLEntity(), HCLNamed {
    override val hcl_name: String = "data.$tf_type.$tf_id"
    override val hcl_ref: String
        get() = hcl_name

    override val owner: HCLNamed?
        get() = this

    var provider by text()

    override fun render(): String {
        return """
            |data "$tf_type" "$tf_id" {
            |${super.render().withIndent()}
            |}
        """.trimMargin()
    }
}
