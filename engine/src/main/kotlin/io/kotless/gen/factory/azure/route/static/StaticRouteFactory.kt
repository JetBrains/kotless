package io.kotless.gen.factory.azure.route.static

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.aws.route.AbstractRouteFactory
import io.kotless.gen.factory.azure.filescontent.LambdaDescription
import io.kotless.gen.factory.azure.info.InfoFactory
import io.kotless.gen.factory.azure.resource.static.StaticResourceFactory

object StaticRouteFactory : GenerationFactory<Application.ApiGateway.StaticRoute, StaticRouteFactory.Output>, AbstractRouteFactory() {
    data class Output(val proxyPart: String)

    override fun mayRun(entity: Application.ApiGateway.StaticRoute, context: GenerationContext) =
        context.output.check(context.webapp, InfoFactory)

    override fun generate(
        entity: Application.ApiGateway.StaticRoute,
        context: GenerationContext
    ): GenerationFactory.GenerationResult<Output> {
        val storageAccount = context.output.get(context.webapp, InfoFactory).storageAccount
        val storageContainer = context.output.get(context.webapp, InfoFactory).storageContainer
        val blobName = context.output.get(context.schema.statics[entity.resource]!!, StaticResourceFactory).blobName

        val lambdaDescriptionFileBody = LambdaDescription.staticRoute(entity, storageAccount, storageContainer, blobName)

        return GenerationFactory.GenerationResult(Output(lambdaDescriptionFileBody))
    }
}
