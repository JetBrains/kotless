package io.kotless.dsl

import com.amazonaws.services.lambda.runtime.Context
import io.kotless.dsl.app.*
import io.kotless.dsl.model.*
import io.kotless.dsl.utils.Json
import io.ktor.server.engine.EngineAPI
import io.ktor.util.pipeline.execute
import kotlinx.coroutines.runBlocking
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
@Suppress("unused")
@EngineAPI
internal class LambdaHandler {
    companion object {
        private val logger = LoggerFactory.getLogger(LambdaHandler::class.java)

        private val engine = KotlessEngine(KotlessEnvironment())

        init {
            engine.start()
        }
    }


    fun handleRequest(input: InputStream, output: OutputStream, @Suppress("UNUSED_PARAMETER") any: Context) {
        val response = try {
            runBlocking {
                val jsonRequest = input.bufferedReader().use { it.readText() }

                logger.info("Started handling request")
                logger.debug("Request is {}", jsonRequest)


                if (jsonRequest.contains("Scheduled Event")) {
                    val event = Json.parse(CloudWatch.serializer(), jsonRequest)
                    if (event.`detail-type` == "Scheduled Event" && event.source == "aws.events") {
                        logger.info("Request is Scheduled Event")
                        return@runBlocking null
                    }
                }

                logger.info("Request is HTTP Event")

                val request = Json.parse(HttpRequest.serializer(), jsonRequest)
                val call = KotlessCall(engine.application, request)

                engine.pipeline.execute(call)

                call.response.toHttp()
            }
        } catch (e: Throwable) {
            logger.error("Error occurred during handle of request and was not caught", e)
            null
        }

        if (response != null) {
            output.write(Json.bytes(HttpResponse.serializer(), response))
        } else {
            logger.info("Got null response")
        }

        logger.info("Ended handling request")
    }
}
