package io.kotless.terraform.provider.aws.resource.s3

import io.kotless.terraform.TFFile
import io.kotless.terraform.TFResource

/**
 * Terraform aws_s3_bucket resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/s3_bucket.html">aws_s3_bucket</a>
 */
class S3Bucket(id: String) : TFResource(id, "aws_s3_bucket") {
    var name by text()
    var acl by text()
}

fun s3_bucket(id: String, configure: S3Bucket.() -> Unit) = S3Bucket(id).apply(configure)

fun TFFile.s3_bucket(id: String, configure: S3Bucket.() -> Unit) {
    add(S3Bucket(id).apply(configure))
}
