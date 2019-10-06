package io.kotless.terraform.infra

import io.kotless.hcl.HCLEntity
import io.kotless.terraform.TFFile
import io.kotless.utils.Text
import io.kotless.utils.withIndent

/** Declaration of Terraform configuration */
class TFConfig : HCLEntity() {
    override fun render(): String {
        return """
            |terraform {
            |${super.render().withIndent(Text.indent)}
            |${(backend?.render() ?: "").withIndent(Text.indent)}
            |}
            """.trimMargin()
    }

    var required_version by text()

    sealed class Backend(val type: String) : HCLEntity() {
        override fun render(): String {
            return """
            |backend "$type" {
            |${super.render().withIndent(Text.indent)}
            |}
            """.trimMargin()
        }


        class S3 : Backend("s3") {
            var bucket by text()
            var key by text()
            var profile by text()
            var region by text()
        }
    }

    var backend: Backend? = null
}

fun terraform(configure: TFConfig.() -> Unit) = TFConfig().apply(configure)

fun TFFile.terraform(configure: TFConfig.() -> Unit) {
    add(TFConfig().apply(configure))
}


