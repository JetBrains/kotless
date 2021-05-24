package io.kotless.gen.factory.azure.route.dynamic

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.aws.route.AbstractRouteFactory
import io.kotless.gen.factory.azure.filescontent.LambdaDescription
import io.kotless.gen.factory.azure.info.InfoFactory
import io.kotless.gen.factory.azure.utils.FilesCreationTf
import io.kotless.terraform.functions.path

object DynamicRouteFactory : GenerationFactory<Application.API.DynamicRoute, DynamicRouteFactory.Output>, AbstractRouteFactory() {
    data class Output(val fileCreationRef: String, val proxyPart: String)

    override fun mayRun(entity: Application.API.DynamicRoute, context: GenerationContext) =
        context.output.check(context.webapp, InfoFactory)

    override fun generate(
        entity: Application.API.DynamicRoute,
        context: GenerationContext
    ): GenerationFactory.GenerationResult<Output> {
        val lambda = context.schema.lambdas[entity.lambda]!!
        val functionAppName = context.names.azure(lambda.name)
        val lambdaDescriptionFileBody = LambdaDescription.body(lambda)

        val resourceName = "route_${entity.path.toString().replace(".", "_").replace("/", "_")}"
        val path = "route_" + entity.path.toString().replace("/", "_")

        val result = FilesCreationTf.localFile(resourceName, lambdaDescriptionFileBody, path(lambda.file.parentFile.resolve(path).resolve("function.json")))
        val proxyPart = LambdaDescription.proxy(path, entity, functionAppName)

        return GenerationFactory.GenerationResult(Output(result.hcl_ref, proxyPart), result)
    }
}
