package io.kotless.dsl.model.events

import io.kotless.InternalAPI
import io.kotless.dsl.utils.JSON
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SQSEventInformation(
    val messageId: String,
    val receiptHandle: String,
    val body: String,
    val attributes: SQSEventAttributes,
    val md5OfBody: String,
    override val awsRegion: String,
    val eventSourceARN: String,
    // TODO: check this field one more time
    val messageAttributes: Map<String, String>
) : AwsEventInformation() {
    override val eventSource: String = "aws:sqs"
    override val parameters: Map<String, String> = mapOf(
        "body" to body,
        "md5OfBody" to md5OfBody
    )
    override val path: String
        get() = eventSourceARN

    companion object {
        @OptIn(InternalAPI::class)
        fun deserialize(record: String): AwsEventInformation {
            return JSON.parse(serializer(), record)
        }
    }

    @Serializable
    data class SQSEventAttributes(
        @SerialName("ApproximateReceiveCount") val approximateReceiveCount: String,
        @SerialName("SentTimestamp") val sentTimestamp: String,
        @SerialName("SequenceNumber") val sequenceNumber: String? = null,
        @SerialName("MessageGroupId") val messageGroupId: String? = null,
        @SerialName("SenderId") val senderId: String,
        @SerialName("MessageDeduplicationId") val messageDeduplicationId: String? = null,
        @SerialName("ApproximateFirstReceiveTimestamp") val approximateFirstReceiveTimestamp: String
    )
}
