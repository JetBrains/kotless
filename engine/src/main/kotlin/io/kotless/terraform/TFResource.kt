package io.kotless.terraform

import io.kotless.hcl.HCLEntity
import io.kotless.hcl.HCLNamed
import io.kotless.utils.withIndent

/** Representation of Terraform Resource */
open class TFResource(val tf_id: String, val tf_type: String) : HCLEntity.Named() {
    override val hcl_name: String = "$tf_type.$tf_id"
    override val hcl_ref: String
        get() = hcl_name

    override val owner: HCLNamed?
        get() = this

    var provider by text()

    var depends_on by textArray()

    class Lifecycle : HCLEntity.Inner("lifecycle") {
        var create_before_destroy by bool(default = true)
    }

    fun lifecycle(configure: Lifecycle.() -> Unit) {
        inner(Lifecycle().apply(configure))
    }

    override fun render(): String {
        return """
            |resource "$tf_type" "$tf_id" {
            |${super.render().withIndent()}
            |}
            """.trimMargin()
    }
}
