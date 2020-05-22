package io.kotless.dsl.utils

import io.kotless.InternalAPI
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

import kotlinx.serialization.json.JsonConfiguration

@InternalAPI
object Json {
    @Suppress("EXPERIMENTAL_API_USAGE")
    val mapper = Json(JsonConfiguration.Default)

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> string(serializer: KSerializer<T>, obj: Any): String = mapper.stringify(serializer as KSerializer<Any>, obj)

    inline fun <reified T : Any> bytes(serializer: KSerializer<T>, obj: Any): ByteArray = string(serializer, obj).toByteArray()

    inline fun <reified T : Any> parse(serializer: KSerializer<T>, serialized: String): T = mapper.parse(serializer, serialized)
}

