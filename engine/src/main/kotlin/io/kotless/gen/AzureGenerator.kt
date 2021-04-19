package io.kotless.gen

import io.kotless.Application
import io.kotless.KotlessConfig
import io.kotless.Schema
import io.kotless.gen.factory.azure.ZipArchiveFactory
import io.kotless.gen.factory.azure.info.InfoFactory
import io.kotless.gen.factory.azure.infra.ProvidersFactory
import io.kotless.gen.factory.azure.resource.dynamic.FunctionFactory
import io.kotless.gen.factory.azure.resource.static.StaticResourceFactory
import io.kotless.gen.factory.azure.route.dynamic.DynamicRouteFactory
import io.kotless.gen.factory.azure.route.static.StaticRouteFactory
import io.kotless.gen.factory.azure.route53.CertificateFactory
import io.kotless.gen.factory.azure.route53.RecordFactory
import io.kotless.gen.factory.azure.route53.ZoneFactory
import io.kotless.gen.factory.azure.storage.StorageFactory
import io.kotless.resource.Lambda
import io.kotless.resource.StaticResource
import io.terraformkt.terraform.TFFile
import kotlin.reflect.KClass

object AzureGenerator {
    private val factories: Map<KClass<*>, Set<GenerationFactory<*, *>>> = mapOf(
        Application::class to setOf(InfoFactory, ZipArchiveFactory, StorageFactory),
        KotlessConfig.Terraform::class to setOf(ProvidersFactory),

        Application.Route53::class to setOf(CertificateFactory, RecordFactory, ZoneFactory),

        StaticResource::class to setOf(StaticResourceFactory),
        Lambda::class to setOf(FunctionFactory),

        Application.ApiGateway.StaticRoute::class to setOf(StaticRouteFactory),
        Application.ApiGateway.DynamicRoute::class to setOf(DynamicRouteFactory)
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
