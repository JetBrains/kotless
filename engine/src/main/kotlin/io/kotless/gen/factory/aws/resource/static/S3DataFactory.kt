package io.kotless.gen.factory.aws.resource.static

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.terraformkt.aws.data.s3.S3Bucket
import io.terraformkt.aws.data.s3.s3_bucket

object S3DataFactory : GenerationFactory<Application.Events.S3, S3DataFactory.Output> {
    data class Output(val s3Bucket: S3Bucket)

    private val cache = mutableMapOf<String, S3Bucket>()

    override fun mayRun(entity: Application.Events.S3, context: GenerationContext) = true

    override fun generate(entity: Application.Events.S3, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val key = context.names.tf(entity.bucket)
        if (key in cache) return GenerationFactory.GenerationResult(Output(cache[key]!!))

        val s3Bucket = s3_bucket(context.names.tf(entity.bucket)) {
            bucket = entity.bucket
        }
        cache[key] = s3Bucket
        return GenerationFactory.GenerationResult(Output(s3Bucket), s3Bucket)
    }
}
