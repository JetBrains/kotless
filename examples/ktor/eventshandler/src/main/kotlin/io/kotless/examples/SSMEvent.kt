package io.kotless.examples

import io.kotless.dsl.model.events.*
import kotlinx.serialization.*


@Serializable
data class SSMAwsEvent(
    val version: String,
    val id: String,
    @SerialName("detail-type")
    val detailType: String? = null,
    @SerialName("source")
    val eventSource: String = "aws.ssm",
    val account: String,
    val time: String,
    val region: String
) : AwsEvent() {

    class SSMAwsEventInformation(val event: SSMAwsEvent) : AwsEventInformation() {
        override val eventSource: String = event.eventSource
        override val path: String = "ssm/path/${event.region}"
        override val parameters: Map<String, String>
            get() = mapOf(
                "version" to event.version,
                "id" to event.id,
                "detailType" to event.detailType,
                "eventSource" to event.eventSource,
                "account" to event.account,
                "time" to event.time,
                "region" to event.time
            ).filterValues { it != null } as Map<String, String>
    }

    override fun records(): List<SSMAwsEventInformation> {
        return listOf(SSMAwsEventInformation(this))
    }
}


object SSMAwsEventInformationGenerator : AwsEventGenerator() {
    override val serializer: KSerializer<out AwsEvent> = SSMAwsEvent.serializer()

    override fun mayDeserialize(jsonObject: String): Boolean = jsonObject.contains("aws.ssm")
}
