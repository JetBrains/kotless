package io.kotless.gen

import io.kotless.*
import io.kotless.gen.factory.apigateway.DeploymentFactory
import io.kotless.gen.factory.apigateway.DomainFactory
import io.kotless.gen.factory.apigateway.RestAPIFactory
import io.kotless.gen.factory.event.ScheduledEventsFactory
import io.kotless.gen.factory.info.InfoFactory
import io.kotless.gen.factory.infra.TFConfigFactory
import io.kotless.gen.factory.infra.TFProvidersFactory
import io.kotless.gen.factory.resource.dynamic.LambdaFactory
import io.kotless.gen.factory.resource.static.StaticResourceFactory
import io.kotless.gen.factory.route.dynamic.DynamicRouteFactory
import io.kotless.gen.factory.route.static.StaticRoleFactory
import io.kotless.gen.factory.route.static.StaticRouteFactory
import io.kotless.gen.factory.route53.CertificateFactory
import io.kotless.gen.factory.route53.RecordFactory
import io.kotless.gen.factory.route53.ZoneFactory
import io.kotless.terraform.TFFile
import kotlin.reflect.KClass

object Generator {
    private val factories: Map<KClass<*>, Set<GenerationFactory<*, *>>> = mapOf(
        Webapp::class to setOf(InfoFactory, StaticRoleFactory),
        KotlessConfig.Terraform::class to setOf(TFConfigFactory, TFProvidersFactory),

        Webapp.ApiGateway::class to setOf(DomainFactory, RestAPIFactory),
        Webapp.ApiGateway.Deployment::class to setOf(DeploymentFactory),
        Webapp.Route53::class to setOf(CertificateFactory, RecordFactory, ZoneFactory),

        StaticResource::class to setOf(StaticResourceFactory),
        Lambda::class to setOf(LambdaFactory),

        Webapp.Events.Scheduled::class to setOf(ScheduledEventsFactory),

        Webapp.ApiGateway.StaticRoute::class to setOf(StaticRouteFactory),
        Webapp.ApiGateway.DynamicRoute::class to setOf(DynamicRouteFactory)
    )

    fun generate(schema: Schema): Set<TFFile> {
        val context = GenerationContext(schema, schema.webapp)

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
