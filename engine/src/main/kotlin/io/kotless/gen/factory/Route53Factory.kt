package io.kotless.gen.factory

import io.kotless.Webapp
import io.kotless.gen.*
import io.kotless.terraform.provider.aws.data.acm.acm_certificate
import io.kotless.terraform.provider.aws.data.route53.route53_zone
import io.kotless.terraform.provider.aws.resource.route53.Route53Record
import io.kotless.terraform.provider.aws.resource.route53.route53_record

object Route53Factory : KotlessFactory<Webapp.Route53, Unit> {
    override fun get(entity: Webapp.Route53, context: KotlessGenerationContext) {
        val cert = acm_certificate(Names.tf(entity.certificate)) {
            domain = entity.certificate
            statuses = arrayOf("ISSUED")
        }

        val zone = route53_zone(Names.tf(entity.zone)) {
            name = entity.zone
            private_zone = false
        }

        val record = route53_record(Names.tf("${entity.alias}.${entity.zone}")) {
            zone_id = zone.zone_id
            name = entity.alias
            type = "A"

            alias = Route53Record.Alias {
                //TODO-tanvd add here request to api gateway
            }
        }

        context.registerEntities(cert, zone, record)
    }
}
