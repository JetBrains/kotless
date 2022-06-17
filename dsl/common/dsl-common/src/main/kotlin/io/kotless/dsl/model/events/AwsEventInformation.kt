package io.kotless.dsl.model.events

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.*
import org.slf4j.LoggerFactory

abstract class AwsEventGenerator {
    abstract fun mayDeserialize(jsonObject: String): Boolean

    abstract val serializer: KSerializer<out AwsEvent>
}

@Serializable(with = AwsEvent.AwsEventSerializer::class)
abstract class AwsEvent {

    abstract fun records(): List<AwsEventInformation>

    companion object {
        private val logger = LoggerFactory.getLogger(AwsEventGenerator::class.java)

        fun isEventRequest(jsonRequest: String): Boolean {
            return eventKSerializers.any { it.mayDeserialize(jsonRequest) }
        }

        val eventKSerializers = mutableListOf<AwsEventGenerator>()
    }

    @Serializer(forClass = AwsEvent::class)
    class AwsEventSerializer : DeserializationStrategy<AwsEvent> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("io.kotless.dsl.model.events.AwsEventInformation")

        private fun selectDeserializer(element: JsonElement): List<DeserializationStrategy<out AwsEvent>> {
            return eventKSerializers.filter { it.mayDeserialize(element.jsonObject.toString()) }.map { it.serializer }
        }

        override fun deserialize(decoder: Decoder): AwsEvent {
            val input = decoder as? JsonDecoder
            val tree = input?.decodeJsonElement() ?: error("")
            val serializers = selectDeserializer(tree)
            serializers.forEach { serializer ->
                @Suppress("UNCHECKED_CAST")
                val actualSerializer = serializer as KSerializer<AwsEvent>
                try {
                    return input.json.decodeFromJsonElement(actualSerializer, tree)
                } catch (e: SerializationException) {
                    logger.warn("Error while deserialization", e)
                }
            }
            error("Failed to define serializer for event: $tree")
        }
    }
}


abstract class AwsEventInformation {
    abstract val parameters: Map<String, String>

    abstract val eventSource: String

    abstract val path: String
}
