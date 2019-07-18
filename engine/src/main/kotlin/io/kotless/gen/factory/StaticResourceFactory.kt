package io.kotless.gen.factory

import io.kotless.StaticResource
import io.kotless.gen.*
import io.kotless.terraform.provider.aws.resource.s3.s3_object

object StaticResourceFactory : KotlessFactory<StaticResource, Unit> {
    override fun get(entity: StaticResource, context: KotlessGenerationContext) {
        val obj = s3_object(Names.tf(entity.bucket, *entity.path.parts.toTypedArray())) {
            bucket = entity.bucket
            key = entity.path.toString()
            source = entity.content.absolutePath
            etag = "md5(file(\"${entity.content.absolutePath}\"))"
            content_type = entity.mime.mimeText
        }

        context.registerOutput(entity, Unit)
        context.registerEntities(obj)
    }
}
