package io.kotless.dsl.events

import io.kotless.MimeType
import io.kotless.dsl.utils.tryRun
import kotlinx.serialization.Serializable

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
        tryRun {
            val (name, value) = it.split("=")
            name to value
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


/** HTTP's response ApiGateway representation */
@Serializable
data class HttpResponse(
        /** Status code to return */
        val statusCode: Int,
        /** Headers to pass to client */
        var headers: HashMap<String, String> = HashMap(),
        /** Payload of response */
        val body: String?
) {
    constructor(statusCode: Int, mime: MimeType, body: String? = null) : this(statusCode, hashMapOf("Content-Type" to mime.mimeText), body)
}
