package io.kotless.gen.factory.info

import io.kotless.KotlessConfig
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.hcl.ref
import io.kotless.terraform.provider.aws.data.info.caller_identity
import io.kotless.terraform.provider.aws.data.info.region

object InfoFactory : GenerationFactory<KotlessConfig.Terraform.AWSProvider, InfoFactory.InfoOutput> {
    data class InfoOutput(val account_id: String, val region_name: String)

    override fun mayRun(entity: KotlessConfig.Terraform.AWSProvider, context: GenerationContext) = true

    override fun generate(entity: KotlessConfig.Terraform.AWSProvider, context: GenerationContext): GenerationFactory.GenerationResult<InfoOutput> {
        val caller_identity = caller_identity("current") {}
        val region = region("current") {}

        return GenerationFactory.GenerationResult(InfoOutput(caller_identity::account_id.ref, region::name.ref), caller_identity, region)
    }
}
