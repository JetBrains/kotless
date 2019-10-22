package io.kotless.dsl.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

import kotlinx.serialization.json.JsonConfiguration

object Json {
    @Suppress("EXPERIMENTAL_API_USAGE")
    val jsonMapper = Json(JsonConfiguration(
        encodeDefaults = true,
        strictMode = false,
        unquoted = false,
        prettyPrint = true,
        indent = "    ",
        useArrayPolymorphism = false,
        classDiscriminator = "kt_class_type"
    ))

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> string(serializer: KSerializer<T>, obj: Any): String = jsonMapper.stringify(serializer as KSerializer<Any>, obj)

    inline fun <reified T : Any> bytes(serializer: KSerializer<T>, obj: Any): ByteArray = string(serializer, obj).toByteArray()

    inline fun <reified T : Any> parse(serializer: KSerializer<T>, serialized: String): T = jsonMapper.parse(serializer, serialized)
}

