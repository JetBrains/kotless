package io.kotless.gen.factory

import io.kotless.MimeType
import io.kotless.Webapp
import io.kotless.gen.*
import io.kotless.terraform.provider.aws.resource.apigateway.api_gateway_rest_api

object ApiGatewayFactory : KotlessFactory<Webapp.ApiGateway, ApiGatewayFactory.ApiGatewayOutput> {
    data class ApiGatewayOutput(val rest_api_arn: String)

    override fun get(entity: Webapp.ApiGateway, context: KotlessGenerationContext) {
        val restApi = api_gateway_rest_api(Names.tf(entity.name)) {
            this.name = Names.aws(entity.name)
            this.binary_media_types = MimeType.values().filter { it.isBinary }.map { it.mimeText }.toTypedArray()
        }

        context.registerOutput(entity, ApiGatewayOutput(restApi.arn))
    }
}
