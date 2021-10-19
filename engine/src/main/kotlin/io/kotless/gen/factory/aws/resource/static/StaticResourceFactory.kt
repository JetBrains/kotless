package io.kotless.gen.factory.aws.resource.static

import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.resource.StaticResource
import io.kotless.terraform.functions.*
import io.terraformkt.aws.resource.s3.s3_bucket_object
import io.terraformkt.hcl.ref

object StaticResourceFactory : GenerationFactory<StaticResource, StaticResourceFactory.Output> {
    data class Output(val key: String, val bucket: String)

    override fun mayRun(entity: StaticResource, context: GenerationContext) = true

    override fun generate(entity: StaticResource, context: GenerationContext): GenerationFactory.GenerationResult<Output> {

        val obj = s3_bucket_object(context.names.tf(context.schema.config.aws.storage.bucket, entity.path.parts)) {
            bucket = context.schema.config.aws.storage.bucket
            key = entity.path.toString()
            source = path(entity.file)
            etag = eval(filemd5(entity.file))
            content_type = entity.mime.mimeText
        }

        return GenerationFactory.GenerationResult(Output(obj::key.ref, obj::bucket.ref), obj)
    }
}
