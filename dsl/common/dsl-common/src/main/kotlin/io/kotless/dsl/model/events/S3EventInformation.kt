package io.kotless.dsl.model.events

import io.kotless.InternalAPI
import io.kotless.dsl.utils.JSON
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class S3EventInformation(
    val eventTime: String,
    val eventName: String,
    override val awsRegion: String,
    val s3: S3Event
) : AwsEventInformation() {
    override val eventSource: String = "aws:s3"
    override val parameters: Map<String, String> = mapOf(
        "bucket_name" to s3.bucket.name,
        "bucket_arn" to s3.bucket.arn,
        "object_name" to s3.s3Object.key,
        "object_eTag" to s3.s3Object.eTag,
        "object_size" to s3.s3Object.size.toString()
    )

    override val path: String
        get() = "${s3.bucket.name}:${eventName}"

    companion object {
        @OptIn(InternalAPI::class)
        fun deserialize(record: String): AwsEventInformation {
            return JSON.parse(serializer(), record)
        }
    }

    @Serializable
    data class S3Event(val bucket: Bucket, @SerialName("object") val s3Object: S3Object) {
        @Serializable
        data class Bucket(val name: String, val arn: String)

        @Serializable
        data class S3Object(val key: String, val size: Long, val eTag: String, val versionId: String? = null)
    }
}
