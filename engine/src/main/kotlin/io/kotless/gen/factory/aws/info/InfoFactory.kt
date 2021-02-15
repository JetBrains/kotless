package io.kotless.gen.factory.aws.info

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.terraformkt.hcl.ref
import io.terraformkt.aws.data.caller.caller_identity
import io.terraformkt.aws.data.region.region
import io.terraformkt.aws.data.s3.s3_bucket

object InfoFactory : GenerationFactory<Application, InfoFactory.Output> {
    data class Output(val account_id: String, val region_name: String, val kotless_bucket_arn: String)

    override fun mayRun(entity: Application, context: GenerationContext) = true

    override fun generate(entity: Application, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val caller_identity = caller_identity("current") {}
        val region = region("current") {}
        val kotless_bucket = s3_bucket(context.names.tf("kotless", "bucket")) {
            bucket = context.schema.config.bucket
        }

        return GenerationFactory.GenerationResult(Output(caller_identity::account_id.ref, region::name.ref, kotless_bucket::arn.ref),
            caller_identity, region, kotless_bucket)
    }
}
