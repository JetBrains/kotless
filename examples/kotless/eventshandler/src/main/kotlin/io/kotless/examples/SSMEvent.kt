package io.kotless.examples

import io.kotless.dsl.model.events.AwsEvent
import io.kotless.dsl.model.events.AwsEventInformation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


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

    data class SSMAwsEventInformation(
        override val parameters: Map<String, String> = mapOf(),
        override val path: String = "ssm/path/example",
        override val eventSource: String
    ) : AwsEventInformation()

    override fun records(): List<SSMAwsEventInformation> {
        return listOf(SSMAwsEventInformation(eventSource = this.eventSource))
    }
}

