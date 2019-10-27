package io.kotless.plugin.gradle.dsl

import io.kotless.KotlessConfig

internal fun KotlessDSL.toSchema(): KotlessConfig {
    return with(config) {
        KotlessConfig(bucket, prefix, KotlessConfig.DSL(dsl.type, dsl.workDirectory), genDirectory,
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

internal fun Webapp.Route53.toSchema(): io.kotless.Webapp.Route53 = io.kotless.Webapp.Route53(zone, alias, certificate)
internal fun Webapp.Deployment.toSchema(): io.kotless.Webapp.ApiGateway.Deployment = io.kotless.Webapp.ApiGateway.Deployment(name, version)
