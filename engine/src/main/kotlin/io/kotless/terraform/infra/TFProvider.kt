package io.kotless.terraform.infra

import io.kotless.hcl.HCLEntity
import io.kotless.hcl.HCLNamed
import io.kotless.terraform.TFFile
import io.kotless.utils.withIndent

/**
 * Representation of Terraform provider
 *
 *  @see <a href="https://www.terraform.io/docs/providers/index.html">All providers</a>
 */
open class TFProvider(val tf_provider: String) : HCLEntity(), HCLNamed {
    var alias by text()

    override val hcl_name: String
        get() = "$tf_provider.$alias"
    override val hcl_ref: String
        get() = hcl_name

    override val owner: HCLNamed?
        get() = this

    class Endpoints(val urls: Map<String, String>) : HCLEntity() {
        override fun render(): String {
            return """
            |endpoints {
            |${urls.entries.joinToString(separator = "\n") { (key, value) -> "$key = \"$value\"" }.withIndent()}
            |}
            """.trimMargin()
        }
    }

    fun endpoints(urls: Map<String, String>) {
        inner(Endpoints(urls))
    }

    override fun render(): String {
        return """
            |provider "$tf_provider" {
            |${super.render().withIndent()}
            |}
            """.trimMargin()
    }
}

/**
 * AWS Terraform provider
 *
 *  @see <a href="https://www.terraform.io/docs/providers/aws/index.html">AWS provider</a>
 */
class AWSProvider : TFProvider("aws") {
    var region by text()
    var profile by text()
    var version by text()

    var skip_credentials_validation by bool()
    var skip_metadata_api_check by bool()
    var skip_requesting_account_id by bool()
}

fun aws_provider(configure: AWSProvider.() -> Unit) = AWSProvider().apply(configure)

fun TFFile.aws_provider(configure: AWSProvider.() -> Unit) {
    add(AWSProvider().apply(configure))
}
