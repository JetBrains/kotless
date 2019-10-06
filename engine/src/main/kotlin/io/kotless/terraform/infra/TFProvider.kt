package io.kotless.terraform.infra

import io.kotless.hcl.HCLEntity
import io.kotless.terraform.TFFile
import io.kotless.utils.Text
import io.kotless.utils.withIndent

open class TFProvider(val tf_provider: String) : HCLEntity() {
    override fun render(): String {
        return """
            |provider "$tf_provider" {
            |${super.render().withIndent(Text.indent)}
            |}
            """.trimMargin()
    }
}

class AWSProvider : TFProvider("aws") {
    var region by text()
    var profile by text()
    var version by text()
}

fun aws_provider(configure: AWSProvider.() -> Unit) = AWSProvider().apply(configure)

fun TFFile.aws_provider(configure: AWSProvider.() -> Unit) {
    add(AWSProvider().apply(configure))
}
