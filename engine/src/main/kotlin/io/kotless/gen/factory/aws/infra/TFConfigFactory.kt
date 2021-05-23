package io.kotless.gen.factory.aws.infra

import io.kotless.KotlessConfig.Cloud.Terraform
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.terraformkt.aws.provider.provider
import io.terraformkt.terraform.TFConfig
import io.terraformkt.terraform.terraform

object TFConfigFactory : GenerationFactory<Terraform<Terraform.Backend.AWS, Terraform.Provider.AWS>, Unit> {
    override fun mayRun(entity: Terraform<Terraform.Backend.AWS, Terraform.Provider.AWS>, context: GenerationContext) = true

    override fun generate(
        entity: Terraform<Terraform.Backend.AWS, Terraform.Provider.AWS>,
        context: GenerationContext
    ): GenerationFactory.GenerationResult<Unit> {
        val terraform = terraform {
            required_version = entity.version
            backend = TFConfig.Backend.S3().apply {
                bucket = entity.backend.storage.bucket
                key = entity.backend.key
                profile = entity.backend.profile
                region = entity.backend.storage.region
            }
        }

        val aws_provider = provider {
            profile = entity.provider.profile
            region = entity.provider.region
            version = entity.provider.version
        }

        return GenerationFactory.GenerationResult(Unit, terraform, aws_provider)
    }
}
