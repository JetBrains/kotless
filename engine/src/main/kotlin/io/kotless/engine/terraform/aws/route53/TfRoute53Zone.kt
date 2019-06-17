package io.kotless.engine.terraform.aws.route53

import io.kotless.engine.terraform.TfData
import io.kotless.engine.terraform.synthesizer.TfGroup

/**
 * Terraform aws_route53_zone data.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/d/route53_zone.html">aws_route53_zone</a>
 */
class TfRoute53ZoneData(tfName: String, name: String, isPrivateZone: Boolean = false) : TfData("aws_route53_zone", tfName) {
    val zone_id = "$tfFullName.zone_id"

    override val dataDef = """
        |    name = "$name"
        |    private_zone = $isPrivateZone
        """

    override val group = TfGroup.Route53
}
