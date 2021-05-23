package io.kotless.dsl.cloud.azure.model

import com.microsoft.azure.functions.HttpRequestMessage
import io.kotless.HttpMethod
import io.kotless.dsl.model.HttpRequest
import java.util.*

fun HttpRequestMessage<Optional<String>>.toRequest(): HttpRequest {
    val path = headers["x-original-path"] ?: ""
    val body = if (body.isPresent) body.get().toByteArray() else null
    val sourceIp = headers["x-forwarded-for"] ?: "192.168.0.1"


    return HttpRequest(
        path, HttpMethod.valueOf(httpMethod.name), queryParameters,
        headers,
        body?.let { HttpRequest.Content(it) },
        HttpRequest.Context("domain_name_mock", "HTTP", sourceIp)
    )
}

class AzureHttpRequest {
}
