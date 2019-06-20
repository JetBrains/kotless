package io.kotless.terraform.provider.aws.data.route53

import io.kotless.terraform.TFData
import io.kotless.terraform.TFFile

/**
 * Terraform aws_route53_zone data.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/d/route53_zone.html">aws_route53_zone</a>
 */
class Route53Zone(id: String) : TFData(id, "aws_route53_zone") {
    val zone_id by text(inner = true)

    var name by text()
    var private_zone by bool()
}

fun route53_zone(id: String, configure: Route53Zone.() -> Unit) = Route53Zone(id).apply(configure)

fun TFFile.route53_zone(id: String, configure: Route53Zone.() -> Unit) {
    add(Route53Zone(id).apply(configure))
}
