package io.kotless.plugin.gradle.dsl

import io.kotless.CloudPlatform
import io.kotless.KotlessConfig

internal fun KotlessDSL.toSchema(): KotlessConfig {
    return with(config) {
        KotlessConfig(
            bucket, prefix,
            cloud!!.toSchema(bucket),
            KotlessConfig.DSL(dsl.typeOrDefault, dsl.resolvedStaticsRoot),
            KotlessConfig.Optimization(
                optimization.mergeLambda,
                KotlessConfig.Optimization.AutoWarm(optimization.autowarm.enable, optimization.autowarm.minutes)
            )
        )
    }
}

internal fun KotlessGradleConfig.CloudGradle<*, *>.toSchema(bucket: String): KotlessConfig.Cloud<*> {
    return when (type) {
        CloudPlatform.AWS -> (this as KotlessGradleConfig.CloudGradle.AWS).toSchema(bucket)
        CloudPlatform.Azure -> (this as KotlessGradleConfig.CloudGradle.Azure).toSchema(bucket)
    }
}

internal fun KotlessGradleConfig.CloudGradle.AWS.toSchema(bucket: String): KotlessConfig.Cloud<*> {
    return KotlessConfig.Cloud.AWS(
        KotlessConfig.Cloud.Terraform.AWS(
            terraform.version,
            KotlessConfig.Cloud.Terraform.Backend.AWS(
                terraform.backend.bucket ?: bucket,
                terraform.backend.key,
                terraform.backend.profile ?: profile,
                terraform.backend.region ?: region
            ),
            KotlessConfig.Cloud.Terraform.Provider.AWS(

                terraform.provider.version,
                terraform.provider.profile ?: profile,
                terraform.provider.region ?: region
            )
        )
    )
}

internal fun KotlessGradleConfig.CloudGradle.Azure.toSchema(bucket: String): KotlessConfig.Cloud<*> {
    return KotlessConfig.Cloud.Azure(
        KotlessConfig.Cloud.Terraform.Azure(
            terraform.version,
            KotlessConfig.Cloud.Terraform.Backend.Azure(
                terraform.backend.containerName,
                terraform.backend.key,
                terraform.backend.resourceGroup,
                terraform.backend.storageAccountName
            ),
            KotlessConfig.Cloud.Terraform.Provider.Azure(
                terraform.provider.version
            )
        )
    )
}

internal fun Webapp.DNS.toSchema(): io.kotless.Application.DNS = io.kotless.Application.DNS(zone, alias, certificate)
internal fun Webapp.Deployment.toSchema(path: String): io.kotless.Application.API.Deployment = io.kotless.Application.API.Deployment(
    name ?: path.trim(':').let { if (it.isBlank()) "root" else it.replace(':', '_') },
    version
)
