package io.kotless.dsl

import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import io.kotless.HttpMethod
import io.kotless.InternalAPI
import io.kotless.dsl.app.http.RouteKey
import io.kotless.dsl.app.http.RoutesDispatcher
import io.kotless.dsl.model.HttpRequest
import org.slf4j.LoggerFactory
import java.util.*

@InternalAPI
class HandlerAzure {
    companion object {
        private val logger = LoggerFactory.getLogger(HandlerAzure::class.java)
    }

    @FunctionName("HttpTrigger")
    fun run(@HttpTrigger(
        name = "req",
        methods = [com.microsoft.azure.functions.HttpMethod.GET, com.microsoft.azure.functions.HttpMethod.POST],
        authLevel = AuthorizationLevel.FUNCTION) request: HttpRequestMessage<Optional<String>>, context: ExecutionContext): HttpResponseMessage {
        val response = try {
            val jsonRequest = if(request.body.isPresent) {
                request.body.get()
            } else {
                ""
            }

            logger.debug("Started handling request")
            logger.trace("Request is {}", jsonRequest)

            Application.init()
            val origPath = request.headers["x-original-path"] ?: ""

            logger.info(origPath)
            val requestContext = HttpRequest.RequestContext(
                origPath,
                origPath,
                accountId = "-1",
                resourceId = "-1",
                stage = "/",
                identity = HttpRequest.RequestContext.RequestIdentity(
                    sourceIp = request.headers["x-forwarded-for"] ?: "192.168.0.1",
                    userAgent = request.headers["User-Agent"]
                ),
                protocol = "HTTP",
                requestTimeEpoch = System.currentTimeMillis(),
                domainName = "domain_name_mock"
            )

            val requestBody = HttpRequest(origPath, origPath, HttpMethod.valueOf(request.httpMethod.name), null, request.queryParameters, null, requestContext, jsonRequest, false)
            val resourceKey = RouteKey(HttpMethod.valueOf(request.httpMethod.name), origPath)

            RoutesDispatcher.dispatch(requestBody, resourceKey)
        } catch (e: Throwable) {
            logger.error("Error occurred during handle of request and was not caught", e)
            return request
                .createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.stackTrace)
                .build()
        }
        logger.debug("Ended handling request")
        val outputResponse = request
            .createResponseBuilder(HttpStatus.valueOf(response.statusCode))
            .body(response.body)
        response.headers.forEach {
            outputResponse.header(it.key, it.value)
        }
        return outputResponse.build()
    }
}
