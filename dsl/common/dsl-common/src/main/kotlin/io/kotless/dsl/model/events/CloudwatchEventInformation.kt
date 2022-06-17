package io.kotless.dsl.model.events

import io.kotless.CloudwatchEventType
import io.kotless.InternalAPI
import kotlinx.serialization.*
import kotlinx.serialization.encoding.Decoder
import org.slf4j.LoggerFactory


class CloudwatchEventInformationGenerator : AwsEventGenerator() {
    override fun mayDeserialize(jsonObject: String): Boolean {
        return jsonObject.contains(CloudwatchEventInformation.eventSource)
    }

    override val serializer: KSerializer<out AwsEvent> = CloudwatchEvent.serializer()
}


@Serializable(with = CloudwatchEvent.CloudwatchEventSerializer::class)
class CloudwatchEvent(private val records: List<CloudwatchEventInformation>) : AwsEvent() {
    override fun records(): List<AwsEventInformation> = records

    @Serializer(forClass = CloudwatchEvent::class)
    object CloudwatchEventSerializer {
        override fun deserialize(decoder: Decoder): CloudwatchEvent {
            val cloudwatchEventInformation = decoder.decodeSerializableValue(CloudwatchEventInformation.serializer())
            return CloudwatchEvent(listOf(cloudwatchEventInformation))
        }
    }
}

@Serializable
data class CloudwatchEventInformation(
    val id: String,
    @SerialName("detail-type") val detailType: String,
    val source: String,
    val account: String,
    val time: String,
    val region: String,
    val resources: List<String>,
    val detail: Detail? = null,
    val version: String? = null
) : AwsEventInformation() {

    override val parameters: Map<String, String> = mapOf(
        "id" to id,
        "detailType" to detailType,
        "source" to source,
        "account" to account,
        "time" to time,
        "region" to region
    )

    override val path: String
        get() {
            val resource = resources.first().dropWhile { it != '/' }.drop(1)
            return if (resource.contains(CloudwatchEventType.General.prefix)) {
                resource.substring(resource.lastIndexOf(CloudwatchEventType.General.prefix))
            } else {
                CloudwatchEventType.Autowarm.prefix
            }
        }

    override val eventSource: String = CloudwatchEventInformation.eventSource

    companion object {
        const val eventSource: String = "aws.events"
    }


    @Serializable
    data class Detail(val instanceGroupId: String? = null)
}
