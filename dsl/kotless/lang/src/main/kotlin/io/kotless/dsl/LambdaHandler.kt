package io.kotless.dsl

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import io.kotless.dsl.app.events.EventsDispatcher
import io.kotless.dsl.app.http.RouteKey
import io.kotless.dsl.app.http.RoutesDispatcher
import io.kotless.dsl.lang.http.serverError
import io.kotless.dsl.model.*
import io.kotless.dsl.utils.Json
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream


/**
 * Kotless Application entrypoint.
 *
 * This entrypoint serves requests of cloud provider.
 *
 * It supports:
 * * ApiGateway Post and Get requests
 * * CloudWatch events (used for warming)
 */
class LambdaHandler : RequestStreamHandler {
    companion object {
        private val logger = LoggerFactory.getLogger(LambdaHandler::class.java)
    }

    override fun handleRequest(input: InputStream, output: OutputStream, @Suppress("UNUSED_PARAMETER") any: Context) {
        val response = try {
            val jsonRequest = input.bufferedReader().use { it.readText() }

            logger.info("Started handling request")
            logger.debug("Request is {}", jsonRequest)

            Application.init()

            if (jsonRequest.contains("Scheduled Event")) {
                val event = Json.parse(CloudWatch.serializer(), jsonRequest)
                if (event.`detail-type` == "Scheduled Event" && event.source == "aws.events") {
                    logger.info("Request is Scheduled Event")
                    EventsDispatcher.process(event)
                    return
                }
            }

            logger.info("Request is HTTP Event")

            val request = Json.parse(HttpRequest.serializer(), jsonRequest)
            val resourceKey = RouteKey(request.method, request.path)
            RoutesDispatcher.dispatch(request, resourceKey)

        } catch (e: Throwable) {
            logger.error("Error occurred during handle of request and was not caught", e)
            serverError("Internal error occurred")
        }

        output.write(Json.bytes(HttpResponse.serializer(), response))
        logger.info("Ended handling request")
    }
}
