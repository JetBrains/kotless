package io.kotless.gen.factory.route53

import io.kotless.Webapp
import io.kotless.gen.*
import io.kotless.gen.factory.apigateway.DomainFactory
import io.kotless.terraform.provider.aws.resource.route53.route53_record

object RecordFactory : GenerationFactory<Webapp.Route53, Unit> {
    override fun mayRun(entity: Webapp.Route53, context: GenerationContext) = context.check(entity, ZoneFactory) && context.check(context.webapp.api, DomainFactory)

    override fun generate(entity: Webapp.Route53, context: GenerationContext): GenerationFactory.GenerationResult<Unit> {
        val zone = context.get(entity, ZoneFactory)
        val domain = context.get(context.webapp.api, DomainFactory)

        val record = route53_record(Names.tf(zone.fqdn)) {
            zone_id = zone.zone_id
            name = entity.alias
            type = "A"

            alias {
                name = domain.domain_name
                zone_id = domain.zone_id
                evaluate_target_health = false
            }
        }

        return GenerationFactory.GenerationResult(Unit, record)
    }
}
