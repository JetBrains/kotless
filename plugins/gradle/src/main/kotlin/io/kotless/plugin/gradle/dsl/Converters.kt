package io.kotless.plugin.gradle.dsl

import io.kotless.CloudPlatform
import io.kotless.KotlessConfig

enum class Stage {
    PreDeploy,
    Deploy,
    PostDeploy
}

internal fun KotlessDSL.toSchema(stage: Stage = Stage.Deploy): KotlessConfig {
    return with(config) {
        KotlessConfig(
            cloud!!.toSchema(stage),
            KotlessConfig.DSL(dsl.typeOrDefault, dsl.resolvedStaticsRoot),
            KotlessConfig.Optimization(
                optimization.mergeLambda,
                KotlessConfig.Optimization.AutoWarm(optimization.autowarm.enable, optimization.autowarm.minutes)
            )
        )
    }
}

internal fun KotlessGradleConfig.CloudGradle<*, *>.toSchema(stage: Stage): KotlessConfig.Cloud<*, *> {
    return when (type) {
        CloudPlatform.AWS -> (this as KotlessGradleConfig.CloudGradle.AWS).toSchema(stage)
        CloudPlatform.Azure -> (this as KotlessGradleConfig.CloudGradle.Azure).toSchema()
    }
}

internal fun KotlessGradleConfig.CloudGradle.AWS.toSchema(stage: Stage): KotlessConfig.Cloud<*, *> {
    return KotlessConfig.Cloud.AWS(
        prefix,
        KotlessConfig.Cloud.Storage.S3(
            storage.bucket,
            storage.region ?: region
        ),
        KotlessConfig.Cloud.Terraform.AWS(
            terraform.version,
            KotlessConfig.Cloud.Terraform.Backend.AWS(
                KotlessConfig.Cloud.Storage.S3(
                    terraform.backend.s3?.bucket ?: storage.bucket,
                    terraform.backend.s3?.region ?: storage.region ?: region
                ),
                when (stage) {
                    Stage.PreDeploy -> terraform.backend.preDeployKey
                    Stage.Deploy -> terraform.backend.key
                    Stage.PostDeploy -> terraform.backend.postDeployKey
                },
                terraform.backend.profile ?: profile,
            ),
            KotlessConfig.Cloud.Terraform.Provider.AWS(
                terraform.provider.version,
                terraform.provider.profile ?: profile,
                terraform.provider.region ?: region
            )
        )
    )
}

internal fun KotlessGradleConfig.CloudGradle.Azure.toSchema(): KotlessConfig.Cloud<*, *> {
    return KotlessConfig.Cloud.Azure(
        prefix,
        KotlessConfig.Cloud.Storage.AzureBlob(
            storage.container,
            storage.storageAccount
        ),
        KotlessConfig.Cloud.Terraform.Azure(
            terraform.version,
            KotlessConfig.Cloud.Terraform.Backend.Azure(
                KotlessConfig.Cloud.Storage.AzureBlob(
                    terraform.backend.blob?.container ?: storage.container,
                    terraform.backend.blob?.storageAccount ?: storage.storageAccount
                ),
                terraform.backend.key,
                terraform.backend.resourceGroup
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
