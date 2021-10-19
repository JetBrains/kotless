package io.kotless.dsl.model

import io.kotless.HttpMethod
import java.nio.charset.Charset

data class HttpRequest(
    val path: String, val method: HttpMethod, val params: Map<String, String>,
    val headers: Map<String, String>,
    val body: Content?,
    val context: Context,
) {
    data class Content(val bytes: ByteArray) {
        val string: String
            get() = bytes.toString(Charset.defaultCharset())

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Content

            if (!bytes.contentEquals(other.bytes)) return false

            return true
        }

        override fun hashCode(): Int {
            return bytes.contentHashCode()
        }
    }

    data class Context(val domain: String, val protocol: String, val sourceIp: String)
}
