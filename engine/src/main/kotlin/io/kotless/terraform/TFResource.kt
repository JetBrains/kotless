package io.kotless.terraform

import io.kotless.hcl.*
import io.kotless.utils.Text
import io.kotless.utils.indent

open class TFResource(val tf_id: String, val tf_type: String) : HCLEntity(), HCLNamed {
    override val hcl_name: String = "$tf_type.$tf_id"
    override val hcl_ref: String
        get() = hcl_name

    override fun render(indentNum: Int): String {
        return """
            |${indent(indentNum)}resource "$tf_type" "$tf_id" {
            |${super.render(indentNum + Text.indent)}
            |${indent(indentNum)}}
        """.trimMargin()
    }
}
