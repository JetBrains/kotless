package io.kotless.terraform.provider.aws.resource.route53

import io.kotless.hcl.HCLEntity
import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

/**
 * Terraform aws_route53_record resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/route53_record.html">aws_route53_record</a>
 */
class Route53Record(id: String) : TFResource(id, "aws_route53_record") {
    var zone_id by text()
    var name by text()
    var type by text()
    var records by textArray()

    class Alias(configure: Alias.() -> Unit = {}) : HCLEntity() {
        init {
            configure()
        }

        val name by text()
        val zone_id by text()
        val evaluate_target_health by bool()
    }

    var alias by entity(default = Alias())
}

fun route53_record(id: String, configure: Route53Record.() -> Unit) = Route53Record(id).apply(configure)

fun TFFile.route53_record(id: String, configure: Route53Record.() -> Unit) {
    add(Route53Record(id).apply(configure))
}
