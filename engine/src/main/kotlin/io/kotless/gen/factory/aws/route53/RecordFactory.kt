package io.kotless.gen.factory.aws.route53

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.aws.apigateway.DomainFactory
import io.terraformkt.aws.resource.route53.route53_record

object RecordFactory : GenerationFactory<Application.DNS, Unit> {
    override fun mayRun(entity: Application.DNS, context: GenerationContext) = context.output.check(entity, ZoneFactory)
        && context.output.check(context.webapp.api, DomainFactory)

    override fun generate(entity: Application.DNS, context: GenerationContext): GenerationFactory.GenerationResult<Unit> {
        val zone = context.output.get(entity, ZoneFactory)
        val domain = context.output.get(context.webapp.api, DomainFactory)

        val record = route53_record(context.names.tf(zone.fqdn)) {
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
