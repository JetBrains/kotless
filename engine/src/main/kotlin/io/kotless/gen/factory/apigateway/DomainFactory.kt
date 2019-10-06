package io.kotless.gen.factory.apigateway

import io.kotless.Webapp
import io.kotless.gen.*
import io.kotless.gen.factory.route53.CertificateFactory
import io.kotless.gen.factory.route53.ZoneFactory
import io.kotless.hcl.ref
import io.kotless.terraform.provider.aws.resource.apigateway.api_gateway_base_path_mapping
import io.kotless.terraform.provider.aws.resource.apigateway.api_gateway_domain_name


object DomainFactory : GenerationFactory<Webapp.ApiGateway, DomainFactory.DomainOutput> {
    data class DomainOutput(val domain_name: String, val zone_id: String)

    override fun mayRun(entity: Webapp.ApiGateway, context: GenerationContext) = context.output.check(entity, RestAPIFactory)
        && context.output.check(context.webapp.route53!!, ZoneFactory)
        && context.output.check(context.webapp.route53!!, CertificateFactory)
        && context.output.check(context.webapp.api.deployment, DeploymentFactory)

    override fun generate(entity: Webapp.ApiGateway, context: GenerationContext): GenerationFactory.GenerationResult<DomainOutput> {
        val zone = context.output.get(context.webapp.route53!!, ZoneFactory)
        val certificate = context.output.get(context.webapp.route53!!, CertificateFactory)
        val api = context.output.get(context.webapp.api, RestAPIFactory)
        val deployment = context.output.get(context.webapp.api.deployment, DeploymentFactory)

        val domain = api_gateway_domain_name(Names.tf(entity.name)) {
            domain_name = zone.fqdn
            certificate_arn = certificate.cert_arn
        }

        val basePath = api_gateway_base_path_mapping(Names.tf(entity.name)) {
            api_id = api.rest_api_id
            stage_name = deployment.stage_name

            domain_name = domain.domain_name
        }

        return GenerationFactory.GenerationResult(DomainOutput(domain::cloudfront_domain_name.ref, domain::cloudfront_zone_id.ref), domain, basePath)
    }
}
