package io.kotless.gen.factory.resource.static

import io.kotless.StaticResource
import io.kotless.gen.*
import io.kotless.terraform.functions.*
import io.kotless.terraform.provider.aws.resource.s3.s3_object

object StaticResourceFactory : GenerationFactory<StaticResource, Unit> {
    override fun mayRun(entity: StaticResource, context: GenerationContext) = true

    override fun generate(entity: StaticResource, context: GenerationContext): GenerationFactory.GenerationResult<Unit> {
        val obj = s3_object(Names.tf(entity.bucket, *entity.path.parts.toTypedArray())) {
            bucket = entity.bucket
            key = entity.path.toString()
            source = path(entity.content)
            etag = md5(file(entity.content))
            content_type = entity.mime.mimeText
        }

        return GenerationFactory.GenerationResult(Unit, obj)
    }
}
