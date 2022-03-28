package io.kotless.gen

import io.kotless.*
import io.kotless.gen.factory.aws.apigateway.*
import io.kotless.gen.factory.aws.event.*
import io.kotless.gen.factory.aws.info.InfoFactory
import io.kotless.gen.factory.aws.infra.ProvidersFactory
import io.kotless.gen.factory.aws.infra.TFConfigFactory
import io.kotless.gen.factory.aws.resource.dynamic.LambdaFactory
import io.kotless.gen.factory.aws.resource.static.*
import io.kotless.gen.factory.aws.route.dynamic.DynamicRouteFactory
import io.kotless.gen.factory.aws.route.static.StaticRoleFactory
import io.kotless.gen.factory.aws.route.static.StaticRouteFactory
import io.kotless.gen.factory.aws.route53.*
import io.kotless.resource.Lambda
import io.kotless.resource.StaticResource
import io.terraformkt.terraform.TFFile
import kotlin.reflect.KClass

object AWSGenerator {
    private val factories: Map<KClass<*>, Set<GenerationFactory<*, *>>> = mapOf(
        Application::class to setOf(InfoFactory, StaticRoleFactory),
        KotlessConfig.Cloud.Terraform.AWS::class to setOf(TFConfigFactory, ProvidersFactory),

        Application.API::class to setOf(DomainFactory, RestAPIFactory),
        Application.API.Deployment::class to setOf(DeploymentFactory),
        Application.DNS::class to setOf(CertificateFactory, RecordFactory, ZoneFactory),

        StaticResource::class to setOf(StaticResourceFactory),
        Lambda::class to setOf(LambdaFactory),

        Application.Events.Scheduled::class to setOf(ScheduledEventsFactory),
        Application.Events.S3::class to setOf(S3DataFactory, S3EventsFactory),
        Application.Events.SQS::class to setOf(SQSDataFactory, SQSEventsFactory),

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

        return context.entities.all().groupBy({ it.name }, { it.hclEntity })
            .map {
                TFFile(it.key, it.value.toMutableList())
            }.toSet()
    }
}
