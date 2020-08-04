package io.kotless.plugin.gradle.dsl

import io.kotless.KotlessConfig

internal fun KotlessDSL.toSchema(): KotlessConfig {
    return with(config) {
        KotlessConfig(bucket, prefix, KotlessConfig.DSL(dsl.typeOrDefault, dsl.resolvedStaticsRoot),
            with(terraform) {
                KotlessConfig.Terraform(
                    version,
                    KotlessConfig.Terraform.Backend(
                        backend.bucket ?: this@toSchema.config.bucket,
                        backend.key,
                        backend.profile ?: profile,
                        backend.region ?: region
                    ),
                    KotlessConfig.Terraform.AWSProvider(
                        provider.version,
                        provider.profile ?: profile,
                        provider.region ?: region
                    )
                )
            },
            KotlessConfig.Optimization(
                optimization.mergeLambda,
                KotlessConfig.Optimization.Autowarm(optimization.autowarm.enable, optimization.autowarm.minutes)
            )
        )
    }
}

internal fun Webapp.Route53.toSchema(): io.kotless.Application.Route53 = io.kotless.Application.Route53(zone, alias, certificate)
internal fun Webapp.Deployment.toSchema(path: String): io.kotless.Application.ApiGateway.Deployment = io.kotless.Application.ApiGateway.Deployment(
    name ?: path.trim(':').let { if (it.isBlank()) "root" else it.replace(':', '_') },
    version
)
