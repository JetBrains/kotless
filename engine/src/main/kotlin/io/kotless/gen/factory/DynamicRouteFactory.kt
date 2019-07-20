package io.kotless.gen.factory

import io.kotless.*
import io.kotless.gen.*
import io.kotless.terraform.provider.aws.resource.apigateway.*

object DynamicRouteFactory : KotlessFactory<Webapp.ApiGateway.DynamicRoute, Unit> {
    override fun get(entity: Webapp.ApiGateway.DynamicRoute, context: KotlessGenerationContext) {
        val api = context.get(entity.apiGateway(context.schema), ApiGatewayFactory)
        val lambda = context.get(entity.lambda, LambdaFactory)

        val resourceId = when {
            entity.path.parts.isEmpty() -> {
                api.root_resource_id
            }
            else -> {
                //FIXME For now all resources are added to root (WIP)
                val resource = api_gateway_resource(Names.tf(entity.path.parts)) {
                    rest_api_id = api.rest_api_arn
                    parent_id = api.root_resource_id
                    path_part = entity.path.parts.last()
                }
                context.registerEntities(resource)
                resource.id
            }
        }


        val method = api_gateway_method(Names.tf(entity.path.parts)) {
            rest_api_id = api.rest_api_arn
            this.resource_id = resourceId

            authorization = "NONE"
            http_method = entity.method.name
        }

        val integration = api_gateway_integration(Names.tf(entity.path.parts)) {
            rest_api_id = api.rest_api_arn
            this.resource_id = resourceId

            http_method = entity.method.name
            integration_http_method = HttpMethod.POST.name

            type = "AWS_PROXY"
            //TODO-tanvd hardcoded region for now
            uri = "arn:aws:apigateway:us-east-1:lambda:path/2015-03-31/functions/${lambda.lambda_arn}/invocations"
        }

        context.registerEntities(method, integration)
        context.registerOutput(entity, Unit)

    }

    private fun Webapp.ApiGateway.DynamicRoute.apiGateway(schema: Schema) = schema.webapps.find { it.api.dynamics.contains(this) }!!.api
}
