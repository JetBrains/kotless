package io.kotless.gen.factory.azure.route.static

import io.kotless.Application
import io.kotless.InternalAPI
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.aws.route.AbstractRouteFactory
import io.kotless.gen.factory.azure.filescontent.LambdaDescription
import io.kotless.gen.factory.azure.info.InfoFactory
import io.kotless.gen.factory.azure.resource.static.StaticResourceFactory

@OptIn(InternalAPI::class)
object StaticRouteFactory : GenerationFactory<Application.API.StaticRoute, StaticRouteFactory.Output>, AbstractRouteFactory() {
    data class Output(val proxyPart: String)

    override fun mayRun(entity: Application.API.StaticRoute, context: GenerationContext) =
        context.output.check(context.webapp, InfoFactory)

    override fun generate(
        entity: Application.API.StaticRoute,
        context: GenerationContext
    ): GenerationFactory.GenerationResult<Output> {
        val storageAccount = context.output.get(context.webapp, InfoFactory).storageAccount
        val storageContainer = context.output.get(context.webapp, InfoFactory).staticStorageContainer
        val blobName = context.output.get(context.schema.statics[entity.resource]!!, StaticResourceFactory).blobName

        val lambdaDescriptionFileBody = LambdaDescription.staticRoute(entity, storageAccount, storageContainer, blobName)

        return GenerationFactory.GenerationResult(Output(lambdaDescriptionFileBody))
    }
}
