package io.kotless.gen.factory.apigateway

import io.kotless.MimeType
import io.kotless.Webapp
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.hcl.ref
import io.kotless.terraform.provider.aws.resource.apigateway.api_gateway_rest_api

object RestAPIFactory : GenerationFactory<Webapp.ApiGateway, RestAPIFactory.RestAPIOutput> {
    data class RestAPIOutput(val rest_api_id: String, val root_resource_id: String)

    override fun mayRun(entity: Webapp.ApiGateway, context: GenerationContext) = true

    override fun generate(entity: Webapp.ApiGateway, context: GenerationContext): GenerationFactory.GenerationResult<RestAPIOutput> {
        val restApi = api_gateway_rest_api(context.names.tf(entity.name)) {
            name = context.names.aws(entity.name)
            binary_media_types = MimeType.binary().map { it.mimeText }.toTypedArray()
        }

        return GenerationFactory.GenerationResult(RestAPIOutput(restApi::id.ref, restApi::root_resource_id.ref), restApi)
    }
}
