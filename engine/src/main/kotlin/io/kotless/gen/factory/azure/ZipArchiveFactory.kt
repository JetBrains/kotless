package io.kotless.gen.factory.azure

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.azure.event.ScheduledEventsFactory
import io.kotless.gen.factory.azure.filescontent.LambdaDescription
import io.kotless.gen.factory.azure.route.dynamic.DynamicRouteFactory
import io.kotless.gen.factory.azure.route.static.StaticRouteFactory
import io.kotless.gen.factory.azure.utils.FilesCreationTf
import io.kotless.terraform.functions.path

object ZipArchiveFactory : GenerationFactory<Application, ZipArchiveFactory.Output> {
    data class Output(val artifactCompleteRef: String)

    override fun mayRun(entity: Application, context: GenerationContext) =
        context.webapp.api.dynamics.all { context.output.check(it, DynamicRouteFactory) }
            && context.webapp.api.statics.all { context.output.check(it, StaticRouteFactory) }
            && entity.events.scheduled.all { context.output.check(it, ScheduledEventsFactory) }

    override fun generate(entity: Application, context: GenerationContext): GenerationFactory.GenerationResult<ZipArchiveFactory.Output> {
        val lambdas = context.schema.lambdas.all
        val directory = lambdas.first().file.parentFile
        val dynamicCreateFile = entity.api.dynamics.map { context.output.get(it, DynamicRouteFactory).fileCreationRef }
        val staticCreateFile = entity.api.statics.map { context.output.get(it, StaticRouteFactory).proxyPart }
        val scheduledCreateFile = entity.events.scheduled.map { context.output.get(it, ScheduledEventsFactory).fileCreationRef }
        val proxyParts = entity.api.dynamics.map { context.output.get(it, DynamicRouteFactory).proxyPart }

        val hostJson = LambdaDescription.host()

        val localSettingsJson = LambdaDescription.localSettings()

        val createLocalSettingsFile = FilesCreationTf.localFile("local_settings_file", localSettingsJson, path(directory.resolve("local.settings.json")))
        val createHostFile = FilesCreationTf.localFile("host_file", hostJson, path(directory.resolve("host.json")))

        val proxyBody = (staticCreateFile + proxyParts).joinToString(",", prefix = "{  \\\"proxies\\\": {", postfix = "}}")
        val result = FilesCreationTf.localFile("proxies_file_creation", proxyBody, path(directory.resolve("proxies.json")))

        val zipFile = FilesCreationTf.zipFile(
            "zip_file",
            directory.path,
            "${directory.parent}/result.zip",
            scheduledCreateFile + dynamicCreateFile + listOf(createHostFile.hcl_ref, createLocalSettingsFile.hcl_ref, result.hcl_ref)
        )

        return GenerationFactory.GenerationResult(Output(zipFile.hcl_ref), zipFile, createLocalSettingsFile, createHostFile, result)
    }
}

