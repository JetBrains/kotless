package io.kotless.dsl.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AwsEvent(@SerialName("Records") val records: List<Record>) {
    @Serializable
    data class  Record(val eventTime: String, val eventName: String, val eventSource: String, val awsRegion: String, val s3: S3Event? = null) {
        @Serializable
        data class S3Event(val bucket: Bucket, @SerialName("object") val s3Object: S3Object) {
            @Serializable
            data class Bucket(val name: String, val arn: String)

            @Serializable
            data class S3Object(val key: String, val size: Long, val eTag: String, val versionId: String? = null)
        }
    }

}
