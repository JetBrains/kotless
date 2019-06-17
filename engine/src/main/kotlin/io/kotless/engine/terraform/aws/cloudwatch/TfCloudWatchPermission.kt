package io.kotless.engine.terraform.aws.cloudwatch

/** CloudWatch actions used by permissions */
enum class TfCloudWatchPermission(val awsAction: String) {
    CreateLogGroup("CreateLogGroup"),
    CreateLogStream("CreateLogStream"),
    GetLogEvents("GetLogEvents"),
    PutLogEvents("PutLogEvents")
}
