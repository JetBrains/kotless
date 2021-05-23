package io.kotless.gen.factory.aws.infra

import io.kotless.KotlessConfig.Cloud.Terraform
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.terraformkt.aws.provider.provider

object ProvidersFactory : GenerationFactory<Terraform<Terraform.Backend.AWS, Terraform.Provider.AWS>, ProvidersFactory.Output> {
    class Output(val us_east_provider: String?)

    override fun mayRun(entity: Terraform<Terraform.Backend.AWS, Terraform.Provider.AWS>, context: GenerationContext) = true

    override fun generate(
        entity: Terraform<Terraform.Backend.AWS, Terraform.Provider.AWS>,
        context: GenerationContext
    ): GenerationFactory.GenerationResult<Output> {
        if (entity.provider.region == "us-east-1") {
            return GenerationFactory.GenerationResult(Output(null))
        }

        val aws_provider = provider {
            alias = "us_east_1"
            profile = entity.provider.profile
            region = "us-east-1"
            version = entity.provider.version
        }

        return GenerationFactory.GenerationResult(Output(aws_provider.hcl_ref), aws_provider)
    }
}
