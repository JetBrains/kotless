package io.kotless.gen

import io.kotless.*
import io.kotless.gen.factory.azure.event.ScheduledEventsFactory
import io.kotless.gen.factory.azure.ZipArchiveFactory
import io.kotless.gen.factory.azure.info.InfoFactory
import io.kotless.gen.factory.azure.infra.TFConfigFactory
import io.kotless.gen.factory.azure.resource.dynamic.FunctionFactory
import io.kotless.gen.factory.azure.resource.static.StaticResourceFactory
import io.kotless.gen.factory.azure.route.dynamic.DynamicRouteFactory
import io.kotless.gen.factory.azure.route.static.StaticRouteFactory
import io.kotless.gen.factory.azure.route53.*
import io.kotless.gen.factory.azure.storage.StorageFactory
import io.kotless.resource.Lambda
import io.kotless.resource.StaticResource
import io.terraformkt.terraform.TFFile
import kotlin.reflect.KClass

@OptIn(InternalAPI::class)
object AzureGenerator {
    private val factories: Map<KClass<*>, Set<GenerationFactory<*, *>>> = mapOf(
        Application::class to setOf(InfoFactory, ZipArchiveFactory, StorageFactory),
        KotlessConfig.Cloud.Terraform.Azure::class to setOf(TFConfigFactory),

        Application.DNS::class to setOf(CertificateFactory, RecordFactory, ZoneFactory),

        StaticResource::class to setOf(StaticResourceFactory),
        Lambda::class to setOf(FunctionFactory),

        Application.Events.Scheduled::class to setOf(ScheduledEventsFactory),

        Application.API.StaticRoute::class to setOf(StaticRouteFactory),
        Application.API.DynamicRoute::class to setOf(DynamicRouteFactory)
    )

    fun generate(schema: Schema): Set<TFFile> {
        val context = GenerationContext(schema, schema.application)

        var newExecuted = true
        while (newExecuted) {
            newExecuted = false
            schema.visit { entity ->
                factories[entity::class].orEmpty().forEach { factory ->
                    @Suppress("UNCHECKED_CAST")
                    factory as GenerationFactory<Any, Any>
                    if (!factory.hasRan(entity, context)) {
                        if (factory.mayRun(entity, context)) {
                            factory.run(entity, context)
                            newExecuted = true
                        }
                    }
                }
            }
        }

        return setOf(TFFile(context.webapp.api.name, ArrayList(context.entities.all())))
    }
}
