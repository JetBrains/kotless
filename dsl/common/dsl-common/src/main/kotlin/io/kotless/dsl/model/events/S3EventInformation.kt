package io.kotless.dsl.model.events

import kotlinx.serialization.*

class S3EventInformationGenerator : AwsEventGenerator() {
    override fun mayDeserialize(jsonObject: String): Boolean {
        return jsonObject.contains(S3EventInformation.eventSource)
    }

    override val serializer: KSerializer<out AwsEvent> = S3Event.serializer()
}

@Serializable
class S3Event(@SerialName("Records") val records: List<S3EventInformation>): AwsEvent() {
    override fun records(): List<AwsEventInformation> = records
}

@Serializable
data class S3EventInformation(
    val eventTime: String,
    val eventName: String,
    val awsRegion: String,
    val s3: S3Event,
) : AwsEventInformation() {
    override val parameters: Map<String, String> = mapOf(
        "bucket_name" to s3.bucket.name,
        "bucket_arn" to s3.bucket.arn,
        "object_name" to s3.s3Object.key,
        "object_eTag" to s3.s3Object.eTag,
        "object_size" to s3.s3Object.size.toString()
    )
    override val path: String = "${s3.bucket.name}:${eventName}"
    override val eventSource: String = S3EventInformation.eventSource

    companion object {
        const val eventSource: String = "aws:s3"
    }


    @Serializable
    data class S3Event(val bucket: Bucket, @SerialName("object") val s3Object: S3Object) {
        @Serializable
        data class Bucket(val name: String, val arn: String)

        @Serializable
        data class S3Object(val key: String, val size: Long, val eTag: String, val versionId: String? = null)
    }
}
