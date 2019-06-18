package io.kotless.engine.terraform.aws.route53

import io.kotless.engine.terraform.TfResource
import io.kotless.engine.terraform.synthesizer.TfGroup
import io.kotless.engine.terraform.utils.TfFieldValue
import io.kotless.engine.terraform.utils.tf

/**
 * Terraform aws_route53_record resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/route53_record.html">aws_route53_record</a>
 */
class TfRoute53Record(tfName: String, name: String, zone: TfRoute53ZoneData,
                      type: String,
                      records: List<String>? = null,
                      aliases: List<AliasRecord>? = null) : TfResource("aws_route53_record", tfName) {
    data class AliasRecord(val name: TfFieldValue, val zoneId: TfFieldValue, val evaluateTargetHealth: Boolean = false)

    override val resourceDef: String = """
        |    zone_id = ${tf(zone.zone_id)}
        |    name = "$name"
        |    type = "$type"
        |    ${records?.let { "records = [${it.joinToString { "\"$it\"" }}]" } ?: ""}
            ${aliases?.let {
        it.joinToString(separator = "\n\n") {
            """|    alias {
                   |        name = ${it.name(this@TfRoute53Record)}
                   |        zone_id = ${it.zoneId(this@TfRoute53Record)}
                   |        evaluate_target_health = ${it.evaluateTargetHealth}
                   |    }
                   """
        }
    } ?: ""}
        """

    override val group: TfGroup = TfGroup.Route53
}
