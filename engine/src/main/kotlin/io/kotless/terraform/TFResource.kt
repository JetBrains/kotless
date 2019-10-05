package io.kotless.terraform

import io.kotless.hcl.HCLEntity
import io.kotless.hcl.HCLNamed
import io.kotless.utils.Text
import io.kotless.utils.withIndent

open class TFResource(val tf_id: String, val tf_type: String) : HCLEntity(), HCLNamed {
    override val hcl_name: String = "$tf_type.$tf_id"
    override val hcl_ref: String
        get() = hcl_name

    override val owner: HCLNamed?
        get() = this

    override fun render(): String {
        return """
            |resource "$tf_type" "$tf_id" {
            |${super.render().withIndent(Text.indent)}
            |}
            """.trimMargin()
    }
}
