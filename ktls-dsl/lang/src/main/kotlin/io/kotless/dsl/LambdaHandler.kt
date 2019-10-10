package io.kotless.dsl

import com.amazonaws.services.lambda.runtime.Context
import io.kotless.HttpMethod
import io.kotless.MimeType
import io.kotless.dsl.api.APIDispatcher
import io.kotless.dsl.api.RouteKey
import io.kotless.dsl.events.*
import io.kotless.dsl.lang.http.serverError
import io.kotless.dsl.model.*
import io.kotless.dsl.model.CloudWatch
import io.kotless.dsl.utils.Json
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream


val kotlessLambdaEntrypoint = "${LambdaHandler::class.qualifiedName}::${LambdaHandler::handleRequest.name}"

/**
 * Kotless Application entrypoint.
 *
 * This entrypoint serves requests of cloud provider.
 *
 * It supports:
 * * ApiGateway Post and Get requests
 * * CloudWatch events (used for warming)
 */
internal class LambdaHandler {
    companion object {
        private val logger = LoggerFactory.getLogger(LambdaHandler::class.java)
    }

    fun handleRequest(input: InputStream, output: OutputStream, @Suppress("UNUSED_PARAMETER") any: Context) {
        val response = try {
            val jsonRequest = input.bufferedReader().use { it.readText() }

            logger.info("Started handling request")
            logger.debug("Request is {}", jsonRequest)

            Application.init()

            if (jsonRequest.contains("Scheduled Event")) {
                try {
                    logger.info("Cloudwatch $jsonRequest")
                    val event = Json.parse(CloudWatch.serializer(), jsonRequest)
                    if (event.`detail-type` == "Scheduled Event" && event.source == "aws.events") {
                        EventsDispatcher.process(event)
                        return
                    }
                } catch (e: Exception) {
                    logger.error("Request contained \"Scheduled Event\", but exception happened", e)
                }
            }

            logger.info("Request is HTTP Event")

            val request = Json.parse(HttpRequest.serializer(), jsonRequest)
            val resourceKey = RouteKey(HttpMethod.valueOf(request.httpMethod.toUpperCase()), MimeType.HTML, request.path)
            APIDispatcher.dispatch(request, resourceKey)
        } catch (e: Throwable) {
            logger.error("Error occurred during handle of request and was not caught", e)
            serverError("Internal error occurred")
        }

        output.write(Json.bytes(HttpResponse.serializer(), response))
        logger.info("Ended handling request")
    }
}
