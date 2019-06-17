package io.kotless.engine.terraform.aws.s3

import io.kotless.MimeType
import io.kotless.engine.terraform.TfResource
import io.kotless.engine.terraform.synthesizer.TfGroup
import io.kotless.engine.terraform.utils.TfFieldValue
import io.kotless.engine.terraform.utils.tf
import java.io.File

/**
 * Terraform aws_s3_bucket_object resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/s3_bucket_object.html">aws_s3_bucket_object</a>
 */
class TfS3Object(tfName: String, bucket: TfFieldValue, objectKey: String, source: File, contentType: MimeType? = null) : TfResource("aws_s3_bucket_object", tfName) {

    val bucket = "$tfFullName.bucket"
    val key = "$tfFullName.key"
    val etag = "$tfFullName.etag"
    val source = "$tfFullName.source"

    override val resourceDef = """
        |    bucket = ${bucket()}
        |    key = "$objectKey"
        |    source = "${source.absolutePath}"
        |    etag = ${tf("md5(file(\"${source.absolutePath}\"))")}
        |    ${contentType?.mimeText?.let { "content_type = \"$it\"" } ?: ""}
        """

    override val group = TfGroup.S3
}
