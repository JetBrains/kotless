package io.kotless.gen.factory.route.static

import io.kotless.HttpMethod
import io.kotless.Webapp
import io.kotless.gen.*
import io.kotless.gen.factory.apigateway.RestAPIFactory
import io.kotless.gen.factory.info.InfoFactory
import io.kotless.gen.factory.resource.static.StaticResourceFactory
import io.kotless.gen.factory.route.AbstractRouteFactory
import io.kotless.hcl.HCLEntity
import io.kotless.terraform.provider.aws.resource.apigateway.api_gateway_integration
import io.kotless.terraform.provider.aws.resource.apigateway.api_gateway_method
import io.kotless.terraform.provider.aws.resource.apigateway.response.api_gateway_integration_response

object StaticRouteFactory : GenerationFactory<Webapp.ApiGateway.StaticRoute, Unit>, AbstractRouteFactory() {
    override fun mayRun(entity: Webapp.ApiGateway.StaticRoute, context: GenerationContext) = context.check(context.webapp.api, RestAPIFactory)
        && context.check(entity.resource, StaticResourceFactory)
        && context.check(context.webapp, InfoFactory)
        && context.check(context.webapp, StaticRoleFactory)

    override fun generate(entity: Webapp.ApiGateway.StaticRoute, context: GenerationContext): GenerationFactory.GenerationResult<Unit> {
        val api = context.get(context.webapp.api, RestAPIFactory)
        val resource = context.get(entity.resource, StaticResourceFactory)
        val info = context.get(context.webapp, InfoFactory)
        val static_role = context.get(context.webapp, StaticRoleFactory)

        val resourceId = getResource(entity.path, api, context)

        val method = api_gateway_method(Names.tf(entity.path.parts).ifBlank { "root_resource" }) {
            rest_api_id = api.rest_api_id
            resource_id = resourceId

            authorization = "NONE"
            http_method = entity.method.name
        }

        val response = api_gateway_integration_response(Names.tf(entity.path.parts).ifBlank { "root_resource" }) {
            rest_api_id = api.rest_api_id
            resource_id = resourceId
            http_method = entity.method.name
            status_code = 200
            response_parameters = object : HCLEntity() {
                val contentType by text(name = "method.response.header.Content-Type", default = "integration.response.header.Content-Type")
                val contentLength by text(name = "method.response.header.Content-Length", default = "integration.response.header.Content-Length")
            }
        }

        val integration = api_gateway_integration(Names.tf(entity.path.parts).ifBlank { "root_resource" }) {
            rest_api_id = api.rest_api_id
            resource_id = resourceId

            http_method = entity.method.name
            integration_http_method = HttpMethod.GET.name

            type = "AWS"
            uri = "arn:aws:apigateway:${info.region_name}:s3:path/${resource.bucket}/${resource.key}"
            credentials = static_role.role_arn
        }

        return GenerationFactory.GenerationResult(Unit, method, response, integration)
    }
}
