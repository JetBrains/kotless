package io.kotless.gen.factory.route53

import io.kotless.Webapp
import io.kotless.gen.*
import io.kotless.hcl.ref
import io.kotless.terraform.provider.aws.data.route53.route53_zone

object ZoneFactory : GenerationFactory<Webapp.Route53, ZoneFactory.ZoneOutput> {
    data class ZoneOutput(val zone_id: String, val fqdn: String)

    override fun mayRun(entity: Webapp.Route53, context: GenerationContext) = true

    override fun generate(entity: Webapp.Route53, context: GenerationContext): GenerationFactory.GenerationResult<ZoneOutput> {
        val zone = route53_zone(Names.tf(entity.zone)) {
            name = entity.zone
            private_zone = false
        }

        return GenerationFactory.GenerationResult(ZoneOutput(zone::zone_id.ref, "${entity.alias}.${entity.zone}"), zone)
    }
}
