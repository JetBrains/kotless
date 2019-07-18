package io.kotless.gen.factory

import io.kotless.Schema
import io.kotless.Webapp
import io.kotless.gen.*
import io.kotless.hcl.HCLEntity
import io.kotless.terraform.functions.timestamp
import io.kotless.terraform.provider.aws.resource.apigateway.api_gateway_deployment

object DeploymentFactory : KotlessFactory<Webapp.ApiGateway.Deployment, Unit> {
    override fun get(entity: Webapp.ApiGateway.Deployment, context: KotlessGenerationContext) {
        val deployment = api_gateway_deployment(Names.tf(entity.name)) {
            rest_api_id = context.get(entity.restApi(context.schema), ApiGatewayFactory).rest_api_arn
            stage_name = entity.version

            variables = object : HCLEntity() {
                val deployed_at by text(default = timestamp())
            }
        }

        context.registerEntities(deployment)
        context.registerOutput(entity, Unit)
    }

    private fun Webapp.ApiGateway.Deployment.restApi(schema: Schema) = schema.webapps.find { it.api.deployment == this }!!.api
}
