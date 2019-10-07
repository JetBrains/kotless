package io.kotless.gen.factory.infra

import io.kotless.KotlessConfig
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.terraform.infra.aws_provider

object TFProvidersFactory : GenerationFactory<KotlessConfig.Terraform, TFProvidersFactory.TFProvidersOutput> {
    class TFProvidersOutput(val us_east_provider: String?)

    override fun mayRun(entity: KotlessConfig.Terraform, context: GenerationContext) = true

    override fun generate(entity: KotlessConfig.Terraform, context: GenerationContext): GenerationFactory.GenerationResult<TFProvidersOutput> {
        if (entity.aws.region == "us-east-1") {
            return GenerationFactory.GenerationResult(TFProvidersOutput(null))
        }

        val aws_provider = aws_provider {
            alias = "us_east_1"
            profile = entity.aws.profile
            region = "us-east-1"
            version = entity.aws.version
        }

        return GenerationFactory.GenerationResult(TFProvidersOutput(aws_provider.hcl_ref), aws_provider)
    }
}
