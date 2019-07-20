package io.kotless.gen.factory

import io.kotless.*
import io.kotless.gen.*
import io.kotless.terraform.provider.aws.resource.apigateway.*
import io.kotless.terraform.provider.aws.resource.route53.Route53Record
import io.kotless.terraform.provider.aws.resource.route53.route53_record

object ApiGatewayFactory : KotlessFactory<Webapp.ApiGateway, ApiGatewayFactory.ApiGatewayOutput> {
    data class ApiGatewayOutput(val rest_api_arn: String, val root_resource_id: String)

    override fun get(entity: Webapp.ApiGateway, context: KotlessGenerationContext) {
        val route53Entity = entity.route53(context.schema)

        val restApi = api_gateway_rest_api(Names.tf(entity.name)) {
            this.name = Names.aws(entity.name)
            this.binary_media_types = MimeType.values().filter { it.isBinary }.map { it.mimeText }.toTypedArray()
        }

        val route53 = context.get(route53Entity, Route53Factory)

        val domain = api_gateway_domain_name(Names.tf(entity.name)) {
            domain_name = route53.fqdn
            certificate_arn = route53.certificate_arn
        }

        val basePath = api_gateway_base_path_mapping(Names.tf(entity.name)) {
            api_id = restApi.id
            stage_name = context.get(entity.deployment, DeploymentFactory).stage_name

            domain_name = domain.domain_name
        }

        val record = route53_record(Names.tf(route53.fqdn)) {
            zone_id = route53.zone_id
            name = route53Entity.alias
            type = "A"

            alias = Route53Record.Alias {
                name = domain.cloudfront_domain_name
                zone_id = domain.cloudfront_zone_id
                evaluate_target_health = false
            }
        }

        context.registerOutput(entity, ApiGatewayOutput(restApi.arn, restApi.root_resource_id))
        context.registerEntities(restApi, domain, basePath, record)
    }

    //TODO-tanvd should be nullable?
    private fun Webapp.ApiGateway.route53(schema: Schema): Webapp.Route53 = schema.webapps.find { it.api == this }!!.route53!!
}
