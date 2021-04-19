package io.kotless.gen.factory.aws.infra

import io.kotless.KotlessConfig
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.terraformkt.aws.provider.provider

object ProvidersFactory : GenerationFactory<KotlessConfig.Terraform, ProvidersFactory.Output> {
    class Output(val us_east_provider: String?)

    override fun mayRun(entity: KotlessConfig.Terraform, context: GenerationContext) = true

    override fun generate(entity: KotlessConfig.Terraform, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        if (entity.aws.region == "us-east-1") {
            return GenerationFactory.GenerationResult(Output(null))
        }

        val aws_provider = provider {
            alias = "us_east_1"
            profile = entity.aws.profile
            region = "us-east-1"
            version = entity.aws.version
        }

        return GenerationFactory.GenerationResult(Output(aws_provider.hcl_ref), aws_provider)
    }
}
