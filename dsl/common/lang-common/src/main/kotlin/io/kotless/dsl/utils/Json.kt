package io.kotless.dsl.utils

import io.kotless.InternalAPI
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

@InternalAPI
object Json {
    @Suppress("EXPERIMENTAL_API_USAGE")
    val mapper = Json {
        ignoreUnknownKeys = true
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> string(serializer: KSerializer<T>, obj: Any): String = mapper.encodeToString(serializer as KSerializer<Any>, obj)

    inline fun <reified T : Any> bytes(serializer: KSerializer<T>, obj: Any): ByteArray = string(serializer, obj).toByteArray()

    inline fun <reified T : Any> parse(serializer: KSerializer<T>, serialized: String): T = mapper.decodeFromString(serializer, serialized)
}

