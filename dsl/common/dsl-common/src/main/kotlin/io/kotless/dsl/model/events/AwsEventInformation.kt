package io.kotless.dsl.model.events

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.*

@OptIn(InternalSerializationApi::class)
@Serializable(with = AwsEventInformation.AwsEventInformationSerializer::class)
abstract class AwsEventInformation {
    abstract val eventSource: String
    abstract val awsRegion: String
    abstract val parameters: Map<String, String>

    companion object {
        val eventSerializers = mutableMapOf<String, (String) -> AwsEventInformation>()
    }

    @Serializer(forClass = AwsEventInformation::class)
    object AwsEventInformationSerializer {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("io.kotless.dsl.model.events.AwsEventInformation")

        override fun deserialize(decoder: Decoder): AwsEventInformation {

            val input = decoder as? JsonDecoder ?: throw SerializationException("Expected Json Input")

            val body = input.decodeJsonElement() as? JsonObject ?: throw SerializationException("Expected JsonObject")
            val eventSource = body["eventSource"] ?: throw SerializationException("Expected \"eventSource\" field in the AWS event")

            return eventSerializers[eventSource.jsonPrimitive.content]?.invoke(body.jsonObject.toString())!!

        }
    }

    abstract val path: String
}
