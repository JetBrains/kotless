package io.kotless.terraform.infra

import io.kotless.hcl.HCLEntity
import io.kotless.terraform.TFFile
import io.kotless.utils.withIndent

class TFLocals : HCLEntity.Named() {

    var variables by entity<HCLEntity>()

    override val hcl_name: String = "locals"

    override val hcl_ref: String
        get() = hcl_name

    override fun render(): String = """
        |$hcl_name {
        |${variables.render().withIndent()}
        |}
        """.trimMargin()
}

fun locals(configure: TFLocals.() -> Unit) = TFLocals().apply(configure)

fun TFFile.locals(configure: TFLocals.() -> Unit) {
    add(TFLocals().apply(configure))
}
