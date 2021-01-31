package io.kotless.local.handler

import io.kotless.HttpMethod
import io.kotless.dsl.LambdaHandler
import io.kotless.dsl.model.HttpRequest
import io.kotless.dsl.model.HttpResponse
import io.kotless.dsl.utils.Json
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import java.io.ByteArrayOutputStream
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


internal class DynamicHandler(private val handler: LambdaHandler) : AbstractHandler() {
    override fun handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
        val apiRequest = HttpRequest(
            resource = request.requestURI,
            path = request.requestURI,
            method = HttpMethod.valueOf(request.method),
            myHeaders = request.headerNames.asSequence().map { it to request.getHeader(it) }.toMap(),
            myQueryStringParameters = request.parameterNames.asSequence().map { it to request.getParameter(it) }.toMap(),
            pathParameters = emptyMap(),
            requestContext = HttpRequest.RequestContext(
                resourcePath = request.requestURI,
                path = request.requestURI,
                accountId = "-1",
                resourceId = "-1",
                stage = "/",
                identity = HttpRequest.RequestContext.RequestIdentity(
                    sourceIp = request.remoteAddr,
                    userAgent = request.getHeader("User-Agent")
                ),
                protocol = request.protocol,
                requestTimeEpoch = System.currentTimeMillis(),
                domainName = request.serverName
            ),
            myBody = request.reader.readText(),
            isBase64Encoded = false
        )

        val output = ByteArrayOutputStream()
        handler.handleRequest(
            input = Json.string(HttpRequest.serializer(), apiRequest).byteInputStream(),
            output = output,
            any = null
        )

        val apiResponse = Json.parse(HttpResponse.serializer(), output.toString(Charsets.UTF_8.name()))

        response.apply {
            status = apiResponse.statusCode

            for ((name, value) in apiResponse.headers) {
                setHeader(name, value)
            }

            val apiResponseBody = apiResponse.body
            if (apiResponseBody != null) {
                if (apiResponse.isBase64Encoded) {
                    outputStream.write(Base64.getDecoder().decode(apiResponseBody))
                } else {
                    outputStream.write(apiResponseBody.toByteArray(Charsets.UTF_8))
                }
            }
        }

        baseRequest.isHandled = true
    }

}
