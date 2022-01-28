package io.kotless.dsl.model

import io.kotless.dsl.model.events.AwsEventInformation
import kotlinx.serialization.*
import kotlinx.serialization.builtins.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.*

@Serializable(with = AwsEvent.AwsEventSerializer::class)
data class AwsEvent(@SerialName("Records") val records: List<Record>) {

    @Serializer(forClass = AwsEvent::class)
    object AwsEventSerializer {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("io.kotless.dsl.model.AwsEvent")

        override fun deserialize(decoder: Decoder): AwsEvent {
            val records = decoder.decodeSerializableValue(MapSerializer(String.serializer(), ListSerializer(AwsEventInformation.serializer())))["Records"]!!
            return AwsEvent(records.map {
                Record(
                    eventSource = it.eventSource,
                    awsRegion = it.awsRegion,
                    event = it
                )
            })
        }
    }


    @Serializable
    data class Record(val eventSource: String, val awsRegion: String, val event: AwsEventInformation)

}
