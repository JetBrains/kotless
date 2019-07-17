package io.kotless.terraform.provider.aws.resource.s3

import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

/**
 * Terraform aws_s3_bucket_object resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/s3_bucket_object.html">aws_s3_bucket_object</a>
 */
class S3Object(id: String) : TFResource(id, "aws_s3_bucket_object") {
    var bucket by text()
    var key by text()
    var source by text()
    var etag by text()
    var content_type by text()
}

fun s3_object(id: String, configure: S3Object.() -> Unit) = S3Object(id).apply(configure)

fun TFFile.s3_object(id: String, configure: S3Object.() -> Unit) {
    add(S3Object(id).apply(configure))
}
