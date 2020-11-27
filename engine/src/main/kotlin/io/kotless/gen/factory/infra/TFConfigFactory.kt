package io.kotless.gen.factory.infra

import io.kotless.KotlessConfig
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.terraformkt.aws.provider.provider
import io.terraformkt.terraform.TFConfig
import io.terraformkt.terraform.terraform

object TFConfigFactory : GenerationFactory<KotlessConfig.Terraform, Unit> {
    override fun mayRun(entity: KotlessConfig.Terraform, context: GenerationContext) = true

    override fun generate(entity: KotlessConfig.Terraform, context: GenerationContext): GenerationFactory.GenerationResult<Unit> {
        val terraform = terraform {
            required_version = entity.version
            backend = TFConfig.Backend.S3().apply {
                bucket = entity.backend.bucket
                key = entity.backend.key
                profile = entity.backend.profile
                region = entity.backend.region
            }
        }

        val aws_provider = provider {
            profile = entity.aws.profile
            region = entity.aws.region
            version = entity.aws.version
        }

        return GenerationFactory.GenerationResult(Unit, terraform, aws_provider)
    }
}
