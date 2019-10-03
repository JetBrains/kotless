package io.kotless.gen.factory.route.dynamic

import io.kotless.*
import io.kotless.gen.*
import io.kotless.gen.factory.apigateway.RestAPIFactory
import io.kotless.gen.factory.resource.dynamic.LambdaFactory
import io.kotless.terraform.provider.aws.resource.apigateway.*

object DynamicRouteFactory : GenerationFactory<Webapp.ApiGateway.DynamicRoute, Unit> {
    private val allResources = HashMap<URIPath, ApiGatewayResource>()

    override fun mayRun(entity: Webapp.ApiGateway.DynamicRoute, context: GenerationContext) = context.check(context.webapp.api, RestAPIFactory)
        && context.check(entity.lambda, LambdaFactory)

    override fun generate(entity: Webapp.ApiGateway.DynamicRoute, context: GenerationContext): GenerationFactory.GenerationResult<Unit> {
        val api = context.get(context.webapp.api, RestAPIFactory)
        val lambda = context.get(entity.lambda, LambdaFactory)

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
                    rest_api_id = api.rest_api_id
                    parent_id = prevResourceId
                    path_part = entity.path.parts.last()
                }

                allResources[entity.path] = resource

                context.registerEntities(resource)

                resource.id
            }
        }


        val method = api_gateway_method(Names.tf(entity.path.parts)) {
            rest_api_id = api.rest_api_id
            resource_id = resourceId

            authorization = "NONE"
            http_method = entity.method.name
        }

        val integration = api_gateway_integration(Names.tf(entity.path.parts)) {
            rest_api_id = api.rest_api_id
            resource_id = resourceId

            http_method = entity.method.name
            integration_http_method = HttpMethod.POST.name

            type = "AWS_PROXY"
            //TODO-tanvd hardcoded region for now
            uri = "arn:aws:apigateway:us-east-1:lambda:path/2015-03-31/functions/${lambda.lambda_arn}/invocations"
        }

        return GenerationFactory.GenerationResult(Unit, method, integration)
    }
}
