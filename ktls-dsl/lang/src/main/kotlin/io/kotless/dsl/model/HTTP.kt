package io.kotless.dsl.model

import io.kotless.MimeType
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.collections.HashMap

/** HTTP's request ApiGateway representation */
@Serializable
data class HttpRequest(
    val resource: String,
    val path: String,
    val httpMethod: String,
    val headers: Map<String, String>?,
    val queryStringParameters: Map<String, String>?,
    val pathParameters: Map<String, String>?,
    val requestContext: RequestContext,
    val body: String?
) {
    private val bodyPostParamsMap = body?.split("&")?.mapNotNull {
        try {
            val (name, value) = it.split("=")
            name to value
        } catch (e: Throwable) {
            null
        }
    }?.toMap()

    val allParams = queryStringParameters.orEmpty() + bodyPostParamsMap.orEmpty()

    @Serializable
    data class RequestContext(
        val identity: RequestIdentity,
        val stage: String,
        val path: String,
        val protocol: String?,
        val requestTimeEpoch: Long,
        val domainName: String
    ) {
        @Serializable
        data class RequestIdentity(val sourceIp: String, val userAgent: String)
    }
}


/**
 * HTTP's response ApiGateway representation
 *
 * @param statusCode status code to return
 * @param headers headers to pass to client
 * @param body payload of response
 */
@Serializable
data class HttpResponse(val statusCode: Int, val headers: HashMap<String, String> = HashMap(), val body: String?, val isBase64Encoded: Boolean) {
    constructor(statusCode: Int, mime: MimeType, body: String? = null) : this(statusCode, hashMapOf("Content-Type" to mime.mimeText), body, false)
    constructor(statusCode: Int, mime: MimeType, body: ByteArray) : this(statusCode, hashMapOf("Content-Type" to mime.mimeText), Base64.getEncoder().encodeToString(body), true) {
        require(mime.isBinary) { "Base64 encoded response can be used only for binary types" }
    }
}
