package io.kotless.gen.factory

import io.kotless.StaticResource
import io.kotless.gen.*
import io.kotless.hcl.HCLNamed
import io.kotless.terraform.provider.aws.resource.s3.s3_object

object StaticResourceFactory : KotlessFactory<StaticResource> {
    override fun get(entity: StaticResource, context: KotlessGenerationContext): Set<HCLNamed> {
        val obj = s3_object(Names.tf(entity.bucket, *entity.path.parts.toTypedArray())) {
            bucket = entity.bucket
            key = entity.path.toString()
            source = entity.content.absolutePath
            content_type = entity.mime.mimeText
        }

        return setOf(obj)
    }
}
