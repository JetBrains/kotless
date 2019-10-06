package io.kotless.gen

import io.kotless.*
import io.kotless.gen.factory.apigateway.*
import io.kotless.gen.factory.info.InfoFactory
import io.kotless.gen.factory.infra.TFConfigFactory
import io.kotless.gen.factory.resource.dynamic.AutowarmFactory
import io.kotless.gen.factory.resource.dynamic.LambdaFactory
import io.kotless.gen.factory.resource.static.StaticResourceFactory
import io.kotless.gen.factory.route.dynamic.DynamicRouteFactory
import io.kotless.gen.factory.route.static.StaticRouteFactory
import io.kotless.gen.factory.route53.*
import io.kotless.terraform.TFFile
import kotlin.reflect.KClass

object Generator {
    private val factories: Map<KClass<*>, Set<GenerationFactory<*, *>>> = mapOf(
        KotlessConfig.Terraform.AWSProvider::class to setOf(InfoFactory),
        KotlessConfig.Terraform::class to setOf(TFConfigFactory),

        Webapp.ApiGateway::class to setOf(DomainFactory, RestAPIFactory),
        Webapp.ApiGateway.Deployment::class to setOf(DeploymentFactory),
        Webapp.Route53::class to setOf(CertificateFactory, RecordFactory, ZoneFactory),

        StaticResource::class to setOf(StaticResourceFactory),
        Lambda::class to setOf(LambdaFactory, AutowarmFactory),

        Webapp.ApiGateway.StaticRoute::class to setOf(StaticRouteFactory),
        Webapp.ApiGateway.DynamicRoute::class to setOf(DynamicRouteFactory)
    )

    fun generate(schema: Schema): Set<TFFile> {
        val contexts = schema.webapps.map { webapp ->
            val context = GenerationContext(schema, webapp)

            var newExecuted = true
            while (newExecuted) {
                newExecuted = false
                //FIXME it should visit only webapp
                schema.visit { entity ->
                    println("Visiting entity ${entity::class.qualifiedName}")
                    factories[entity::class].orEmpty().forEach { factory ->
                        factory as GenerationFactory<Any, Any>
                        if (!factory.hasRan(entity, context)) {
                            if (factory.mayRun(entity, context)) {
                                println("Factory run ${factory::class.qualifiedName}")
                                factory.run(entity, context)
                                newExecuted = true
                            }
                        }
                    }
                }
            }

            context
        }

        return contexts.map { TFFile(it.webapp.api.name, ArrayList(it.entities)) }.toSet()

    }
}
