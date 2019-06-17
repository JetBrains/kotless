package io.kotless.engine.terraform.aws.s3

import io.kotless.engine.terraform.TfData
import io.kotless.engine.terraform.TfResource
import io.kotless.engine.terraform.synthesizer.TfGroup

/**
 * Terraform aws_s3_bucket resource.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/r/s3_bucket.html">aws_s3_bucket</a>
 */
class TfS3Bucket(tfName: String, awsName: String, isPrivate: Boolean = true) : TfResource("aws_s3_bucket", tfName) {
    val name = "$tfFullName.bucket"
    override val resourceDef = """
     |    name = "$awsName"
     |    acl = "${if (isPrivate) "private" else "public-read"}"
    """

    override val group = TfGroup.S3
}

/**
 * Terraform aws_s3_bucket data.
 *
 * @see <a href="https://www.terraform.io/docs/providers/aws/d/s3_bucket.html">aws_s3_bucket</a>
 */
class TfS3BucketData(tfName: String, bucket: String) : TfData("aws_s3_bucket", tfName) {
    val arn = "$tfFullName.arn"
    val name = "$tfFullName.bucket"

    override val dataDef = """
      |    bucket = "$bucket"
        """

    override val group = TfGroup.S3
}
