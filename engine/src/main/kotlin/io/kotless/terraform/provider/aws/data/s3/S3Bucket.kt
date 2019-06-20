package io.kotless.terraform.provider.aws.data.s3

import io.kotless.terraform.TFData
import io.kotless.terraform.TFFile

/**
 * Terraform aws_s3_bucket resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/s3_bucket.html">aws_s3_bucket</a>
 */
class S3Bucket(id: String) : TFData(id, "aws_s3_bucket") {
    val arn by text(inner = true)

    var bucket by text()
}

fun s3_bucket(id: String, configure: S3Bucket.() -> Unit) = S3Bucket(id).apply(configure)

fun TFFile.s3_bucket(id: String, configure: S3Bucket.() -> Unit) {
    add(S3Bucket(id).apply(configure))
}
