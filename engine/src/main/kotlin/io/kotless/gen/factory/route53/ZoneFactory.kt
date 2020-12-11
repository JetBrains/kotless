package io.kotless.gen.factory.route53

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.terraformkt.hcl.ref
import io.terraformkt.aws.data.route53.route53_zone

object ZoneFactory : GenerationFactory<Application.Route53, ZoneFactory.Output> {
    data class Output(val zone_id: String, val fqdn: String)

    override fun mayRun(entity: Application.Route53, context: GenerationContext) = true

    override fun generate(entity: Application.Route53, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val zone = route53_zone(context.names.tf(entity.zone)) {
            name = entity.zone
            private_zone = false
        }

        return GenerationFactory.GenerationResult(Output(zone::zone_id.ref, "${entity.alias}.${entity.zone}"), zone)
    }
}
