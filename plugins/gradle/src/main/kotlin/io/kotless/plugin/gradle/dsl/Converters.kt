package io.kotless.plugin.gradle.dsl

import io.kotless.KotlessConfig
import io.kotless.Webapp

internal fun KotlessDsl.toSchema(): KotlessConfig {
    return KotlessConfig(
        kotlessConfig.bucket, kotlessConfig.resourcePrefix, kotlessConfig.workDirectory, kotlessConfig.genDirectory,
        KotlessConfig.Terraform(
            kotlessConfig.terraform.version,
            KotlessConfig.Terraform.Backend(
                kotlessConfig.terraform.backend.bucket, kotlessConfig.terraform.backend.key, kotlessConfig.terraform.profile, kotlessConfig.terraform.region
            ),
            KotlessConfig.Terraform.AWSProvider(
                kotlessConfig.terraform.provider.version, kotlessConfig.terraform.profile, kotlessConfig.terraform.region
            )
        ),
        KotlessConfig.Optimization(kotlessConfig.optimization.mergeLambda)
    )
}

internal fun KotlessDsl.Webapp.Route53.toSchema(): Webapp.Route53 = Webapp.Route53(zone, alias, certificate)
internal fun KotlessDsl.Webapp.Deployment.toSchema(): Webapp.ApiGateway.Deployment = Webapp.ApiGateway.Deployment(name, version)
