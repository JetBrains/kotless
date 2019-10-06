package io.kotless.gen.factory.route.dynamic

import io.kotless.HttpMethod
import io.kotless.Webapp
import io.kotless.gen.*
import io.kotless.gen.factory.apigateway.RestAPIFactory
import io.kotless.gen.factory.info.InfoFactory
import io.kotless.gen.factory.resource.dynamic.LambdaFactory
import io.kotless.gen.factory.route.AbstractRouteFactory
import io.kotless.terraform.provider.aws.resource.apigateway.api_gateway_integration
import io.kotless.terraform.provider.aws.resource.apigateway.api_gateway_method
import io.kotless.terraform.provider.aws.resource.lambda.lambda_permission

object DynamicRouteFactory : GenerationFactory<Webapp.ApiGateway.DynamicRoute, Unit>, AbstractRouteFactory() {
    override fun mayRun(entity: Webapp.ApiGateway.DynamicRoute, context: GenerationContext) = context.output.check(context.webapp.api, RestAPIFactory)
        && context.output.check(entity.lambda, LambdaFactory)
        && context.output.check(context.webapp, InfoFactory)

    override fun generate(entity: Webapp.ApiGateway.DynamicRoute, context: GenerationContext): GenerationFactory.GenerationResult<Unit> {
        val api = context.output.get(context.webapp.api, RestAPIFactory)
        val lambda = context.output.get(entity.lambda, LambdaFactory)
        val info = context.output.get(context.webapp, InfoFactory)

        val resourceId = getResource(entity.path, api, context)

        val method = api_gateway_method(Names.tf(entity.path.parts).ifBlank { "root_resource" }) {
            rest_api_id = api.rest_api_id
            resource_id = resourceId

            authorization = "NONE"
            http_method = entity.method.name
        }

        val permission = lambda_permission(Names.tf(entity.path.parts).ifBlank { "root_resource" }) {
            statement_id = Names.aws(entity.path.parts).ifBlank { "root_resource" }
            action = "lambda:InvokeFunction"
            function_name = lambda.lambda_arn
            principal = "apigateway.amazonaws.com"
            source_arn = "arn:aws:execute-api:${info.region_name}:${info.account_id}:${api.rest_api_id}/*/${method.http_method}/${entity.path}"
        }

        val integration = api_gateway_integration(Names.tf(entity.path.parts).ifEmpty { "root_resource" }) {
            rest_api_id = api.rest_api_id
            resource_id = resourceId

            http_method = entity.method.name
            integration_http_method = HttpMethod.POST.name

            type = "AWS_PROXY"
            uri = "arn:aws:apigateway:${info.region_name}:lambda:path/2015-03-31/functions/${lambda.lambda_arn}/invocations"
        }

        return GenerationFactory.GenerationResult(Unit, method, integration, permission)
    }
}
