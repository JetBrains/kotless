package io.kotless.engine.terraform.aws.s3

/** S3 actions used by permissions */
enum class TfS3Permission(val awsAction: String) {
    DeleteObject("DeleteObject"),
    GetObject("GetObject"),
    PutObject("PutObject"),
    ListBucket("ListBucket");
}
