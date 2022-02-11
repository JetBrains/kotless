package io.kotless.dsl.model.events

import kotlinx.serialization.*
import kotlinx.serialization.builtins.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.*

abstract class AwsEventGenerator {
    abstract fun mayDeserialize(jsonObject: String): Boolean

    abstract val serializer: KSerializer<out AwsEvent>
}

@Serializable(with = AwsEvent.AwsEventSerializer::class)
abstract class AwsEvent() {

    abstract fun records(): List<AwsEventInformation>

    companion object {
        fun isEventRequest(jsonRequest: String): Boolean {
            return eventKSerializers.any { it.mayDeserialize(jsonRequest) }
        }

        val eventKSerializers = mutableListOf<AwsEventGenerator>()
    }

    @Serializer(forClass = AwsEvent::class)
    class AwsEventSerializer : JsonContentPolymorphicSerializer<AwsEvent>(AwsEvent::class) {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("io.kotless.dsl.model.events.AwsEventInformation")

        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out AwsEvent> {
            return eventKSerializers.firstOrNull { it.mayDeserialize(element.jsonObject.toString()) }?.serializer ?: error("Serializer for this object doesn't found")
        }
    }
}


abstract class AwsEventInformation {
    abstract val parameters: Map<String, String>

    abstract val eventSource: String

    abstract val path: String
}
