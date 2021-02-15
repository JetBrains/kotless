package io.kotless.gen.factory.aws.resource.static

import io.kotless.resource.StaticResource
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.terraformkt.hcl.ref
import io.kotless.terraform.functions.eval
import io.kotless.terraform.functions.filemd5
import io.kotless.terraform.functions.path
import io.terraformkt.aws.resource.s3.s3_bucket_object

object StaticResourceFactory : GenerationFactory<StaticResource, StaticResourceFactory.Output> {
    data class Output(val key: String, val bucket: String)

    override fun mayRun(entity: StaticResource, context: GenerationContext) = true

    override fun generate(entity: StaticResource, context: GenerationContext): GenerationFactory.GenerationResult<Output> {

        val obj = s3_bucket_object(context.names.tf(context.schema.config.bucket, entity.path.parts)) {
            bucket = context.schema.config.bucket
            key = entity.path.toString()
            source = path(entity.file)
            etag = eval(filemd5(entity.file))
            content_type = entity.mime.mimeText
        }

        return GenerationFactory.GenerationResult(Output(obj::key.ref, obj::bucket.ref), obj)
    }
}
