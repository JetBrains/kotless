package io.kotless.gen.factory.aws.apigateway

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.aws.route53.CertificateFactory
import io.kotless.gen.factory.aws.route53.ZoneFactory
import io.terraformkt.aws.resource.apigateway.api_gateway_base_path_mapping
import io.terraformkt.aws.resource.apigateway.api_gateway_domain_name
import io.terraformkt.hcl.ref


object DomainFactory : GenerationFactory<Application.API, DomainFactory.Output> {
    data class Output(val domain_name: String, val zone_id: String)

    override fun mayRun(entity: Application.API, context: GenerationContext) = context.output.check(entity, RestAPIFactory)
        && context.webapp.dns != null
        && context.output.check(context.webapp.dns!!, ZoneFactory)
        && context.output.check(context.webapp.dns!!, CertificateFactory)
        && context.output.check(context.webapp.api.deployment, DeploymentFactory)

    override fun generate(entity: Application.API, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val zone = context.output.get(context.webapp.dns!!, ZoneFactory)
        val certificate = context.output.get(context.webapp.dns!!, CertificateFactory)
        val api = context.output.get(context.webapp.api, RestAPIFactory)
        val deployment = context.output.get(context.webapp.api.deployment, DeploymentFactory)

        val domain = api_gateway_domain_name(context.names.tf(entity.name)) {
            domain_name = zone.fqdn
            certificate_arn = certificate.cert_arn
        }

        val basePath = api_gateway_base_path_mapping(context.names.tf(entity.name)) {
            api_id = api.rest_api_id
            stage_name = deployment.stage_name

            domain_name = domain.domain_name
        }

        return GenerationFactory.GenerationResult(Output(domain::cloudfront_domain_name.ref, domain::cloudfront_zone_id.ref), domain, basePath)
    }
}
