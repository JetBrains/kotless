package io.kotless.gen.factory.route.static

import io.kotless.*
import io.kotless.gen.*
import io.kotless.gen.factory.apigateway.RestAPIFactory
import io.kotless.gen.factory.resource.static.StaticResourceFactory
import io.kotless.hcl.HCLEntity
import io.kotless.terraform.provider.aws.resource.apigateway.*
import io.kotless.terraform.provider.aws.resource.apigateway.response.api_gateway_integration_response

object StaticRouteFactory : GenerationFactory<Webapp.ApiGateway.StaticRoute, Unit> {
    private val allResources = HashMap<URIPath, ApiGatewayResource>()

    override fun mayRun(entity: Webapp.ApiGateway.StaticRoute, context: GenerationContext) = context.check(context.webapp.api, RestAPIFactory)
        && context.check(entity.resource, StaticResourceFactory)

    override fun generate(entity: Webapp.ApiGateway.StaticRoute, context: GenerationContext): GenerationFactory.GenerationResult<Unit> {
        val api = context.get(context.webapp.api, RestAPIFactory)
        val resource = context.get(entity.resource, StaticResourceFactory)

        val resourceId = when {
            entity.path.parts.isEmpty() -> {
                api.root_resource_id
            }
            else -> {
                var parts = entity.path.parts
                while (parts.isNotEmpty() && !allResources.containsKey(URIPath(parts))) {
                    parts = parts.dropLast(1)
                }

                val prevResourceId = if (parts.isNotEmpty()) allResources[URIPath(parts)]!!.id else api.root_resource_id

                //FIXME resource should be created by path parts.
                val resource = api_gateway_resource(Names.tf(entity.path.parts)) {
                    rest_api_id = api.rest_api_arn
                    parent_id = prevResourceId
                    path_part = entity.path.parts.last()
                }

                allResources[entity.path] = resource

                context.registerEntities(resource)

                resource.id
            }
        }

        val method = api_gateway_method(Names.tf(entity.path.parts)) {
            rest_api_id = api.rest_api_arn
            resource_id = resourceId

            authorization = "NONE"
            http_method = entity.method.name
        }

        val response = api_gateway_integration_response(Names.tf(entity.path.parts)) {
            rest_api_id = api.rest_api_id
            resource_id = resourceId
            http_method = entity.method.name
            status_code = 200
            response_parameters = object : HCLEntity() {
                val contentType by text(name = "method.response.header.Content-Type", default = "integration.response.header.Content-Type")
                val contentLength by text(name = "method.response.header.Content-Length", default = "integration.response.header.Content-Length")
            }
        }

        val integration = api_gateway_integration(Names.tf(entity.path.parts)) {
            rest_api_id = api.rest_api_arn
            resource_id = resourceId

            http_method = entity.method.name
            integration_http_method = HttpMethod.GET.name

            type = "AWS"
            //TODO-tanvd hardcoded region for now
            uri = "arn:aws:apigateway:us-east-1:s3:path/${resource.bucket}/${resource.key}"
//            credentials
        }

        return GenerationFactory.GenerationResult(Unit, method, response, integration)
    }
}
