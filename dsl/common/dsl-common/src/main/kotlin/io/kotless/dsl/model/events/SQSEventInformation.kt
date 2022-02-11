package io.kotless.dsl.model.events

import kotlinx.serialization.*

class SQSEventInformationGenerator : AwsEventGenerator() {
    override fun mayDeserialize(jsonObject: String): Boolean {
        return jsonObject.contains(SQSEventInformation.eventSource)
    }

    override val serializer: KSerializer<out AwsEvent> = SQSEvent.serializer()
}


@Serializable
class SQSEvent(@SerialName("Records") val records: List<SQSEventInformation>): AwsEvent() {
    override fun records(): List<AwsEventInformation> = records
}


@Serializable
data class SQSEventInformation(
    val messageId: String,
    val receiptHandle: String,
    val body: String,
    val attributes: SQSEventAttributes,
    val md5OfBody: String,
    val awsRegion: String,
    val eventSourceARN: String,
    // TODO: check this field one more time
    val messageAttributes: Map<String, String>
) : AwsEventInformation() {
    override val parameters: Map<String, String> = mapOf(
        "body" to body,
        "md5OfBody" to md5OfBody
    )

    override val path: String = eventSourceARN
    override val eventSource: String = SQSEventInformation.eventSource

    companion object {
        val eventSource: String = "aws:sqs"
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
