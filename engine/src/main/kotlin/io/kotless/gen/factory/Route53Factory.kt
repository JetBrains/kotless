package io.kotless.gen.factory

import io.kotless.Webapp
import io.kotless.gen.*
import io.kotless.terraform.provider.aws.data.acm.acm_certificate
import io.kotless.terraform.provider.aws.data.route53.route53_zone

object Route53Factory : KotlessFactory<Webapp.Route53, Route53Factory.Route53Output> {
    data class Route53Output(val certificate_arn: String, val zone_id: String, val fqdn: String)

    override fun get(entity: Webapp.Route53, context: KotlessGenerationContext) {
        val cert = acm_certificate(Names.tf(entity.certificate)) {
            domain = entity.certificate
            statuses = arrayOf("ISSUED")
        }

        val zone = route53_zone(Names.tf(entity.zone)) {
            name = entity.zone
            private_zone = false
        }

        context.registerOutput(entity, Route53Output(cert.arn, zone.zone_id, "${entity.alias}.${entity.zone}"))
        context.registerEntities(cert, zone)
    }
}
