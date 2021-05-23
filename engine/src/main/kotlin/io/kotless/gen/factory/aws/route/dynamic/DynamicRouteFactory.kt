package io.kotless.gen.factory.aws.route.dynamic

import io.kotless.Application
import io.kotless.HttpMethod
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.aws.apigateway.RestAPIFactory
import io.kotless.gen.factory.aws.info.InfoFactory
import io.kotless.gen.factory.aws.resource.dynamic.LambdaFactory
import io.kotless.gen.factory.aws.route.AbstractRouteFactory
import io.kotless.terraform.functions.link
import io.terraformkt.aws.resource.apigateway.api_gateway_integration
import io.terraformkt.aws.resource.apigateway.api_gateway_method
import io.terraformkt.aws.resource.lambda.lambda_permission

object DynamicRouteFactory : GenerationFactory<Application.API.DynamicRoute, DynamicRouteFactory.Output>, AbstractRouteFactory() {
    data class Output(val integration: String)

    override fun mayRun(entity: Application.API.DynamicRoute, context: GenerationContext) = context.output.check(context.webapp.api, RestAPIFactory)
        && context.output.check(context.schema.lambdas[entity.lambda]!!, LambdaFactory)
        && context.output.check(context.webapp, InfoFactory)

    override fun generate(entity: Application.API.DynamicRoute, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val api = context.output.get(context.webapp.api, RestAPIFactory)
        val lambda = context.output.get(context.schema.lambdas[entity.lambda]!!, LambdaFactory)
        val info = context.output.get(context.webapp, InfoFactory)

        val resourceApi = getResource(entity.path, api, context)

        val tf_name = context.names.tf(entity.path.parts, entity.method.name).ifBlank { "root_resource" }
        val aws_name = context.names.aws(entity.path.parts, entity.method.name).ifBlank { "root_resource" }

        val method = api_gateway_method(tf_name) {
            depends_on = arrayOf(link(resourceApi.ref))

            rest_api_id = api.rest_api_id
            resource_id = resourceApi.id

            authorization = "NONE"
            http_method = entity.method.name
        }

        val permission = lambda_permission(tf_name) {
            statement_id = aws_name
            action = "lambda:InvokeFunction"
            function_name = lambda.lambda_arn
            principal = "apigateway.amazonaws.com"
            source_arn = "arn:aws:execute-api:${info.region_name}:${info.account_id}:${api.rest_api_id}/*/${method.http_method}/${entity.path}"
        }

        val integration = api_gateway_integration(tf_name) {
            depends_on = arrayOf(link(resourceApi.ref))

            rest_api_id = api.rest_api_id
            resource_id = resourceApi.id

            http_method = entity.method.name
            integration_http_method = HttpMethod.POST.name

            type = "AWS_PROXY"
            uri = "arn:aws:apigateway:${info.region_name}:lambda:path/2015-03-31/functions/${lambda.lambda_arn}/invocations"
        }

        return GenerationFactory.GenerationResult(Output(integration.hcl_ref), method, integration, permission)
    }
}
