package io.kotless.dsl.model

import io.kotless.HttpMethod
import io.kotless.MimeType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.collections.HashMap

/** HTTP's request ApiGateway representation */
@Serializable
data class HttpRequest(
    val resource: String,
    val path: String,
    @SerialName("httpMethod") val method: HttpMethod,
    @SerialName("headers") val myHeaders: Map<String, String>?,
    @SerialName("queryStringParameters") val myQueryStringParameters: Map<String, String>?,
    val pathParameters: Map<String, String>?,
    val requestContext: RequestContext,
    @SerialName("body") val myBody: String?,
    private val isBase64Encoded: Boolean
) {

    val headers: Map<String, List<String>>?
        get() = myHeaders?.mapValues { (_, value) -> value.split(",").map { it.trim() } }

    val params = myQueryStringParameters

    val body: ByteArray?
        get() = myBody?.let {
            if (isBase64Encoded) {
                Base64.getDecoder().decode(it)
            } else {
                it.toByteArray()
            }
        }

    @Serializable
    data class RequestContext(
        val resourcePath: String,
        val path: String,
        val accountId: String,
        val resourceId: String,
        val stage: String,
        val identity: RequestIdentity,
        val protocol: String,
        val requestTimeEpoch: Long,
        val domainName: String
    ) {
        //Path to stage is calculated as full path minus path to resource and first /
        val stagePath = path.dropLast(resourcePath.length - 1)

        @Serializable
        data class RequestIdentity(val sourceIp: String, val userAgent: String?)
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

    constructor(statusCode: Int, headers: HashMap<String, String>, body: String) : this(statusCode, headers, body, false)
    constructor(statusCode: Int, headers: HashMap<String, String>, body: ByteArray) : this(statusCode, headers, Base64.getEncoder().encodeToString(body), true)

    constructor(statusCode: Int, mime: MimeType, body: String? = null) : this(statusCode, hashMapOf("Content-Type" to mime.mimeText), body, false)
    constructor(statusCode: Int, mime: MimeType, body: ByteArray)
        : this(statusCode, hashMapOf("Content-Type" to mime.mimeText), Base64.getEncoder().encodeToString(body), true) {
        require(mime.isBinary) { "Base64 encoded response can be used only for binary types" }
    }
}
