package io.kotless.gen.factory.azure.route53

import io.kotless.Application
import io.kotless.KotlessConfig
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.azure.info.InfoFactory
import io.terraformkt.azurerm.data.dns.dns_zone
import io.terraformkt.hcl.ref

object ZoneFactory : GenerationFactory<Application.Route53, ZoneFactory.Output> {
    data class Output(val zone_name: String, val fqdn: String)

    override fun mayRun(entity: Application.Route53, context: GenerationContext) = context.output.check(context.webapp, InfoFactory)

    override fun generate(entity: Application.Route53, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val dnsZone = dns_zone(context.names.tf(entity.zone)) {
            name = entity.zone
            resource_group_name = (context.schema.config.cloudConfig as KotlessConfig.AzureCloudConfig).resourceGroup
        }

        return GenerationFactory.GenerationResult(Output(dnsZone::name.ref, "${entity.alias}.${entity.zone}"), dnsZone)
    }
}
