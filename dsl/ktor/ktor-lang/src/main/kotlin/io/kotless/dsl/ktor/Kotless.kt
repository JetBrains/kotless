package io.kotless.dsl.ktor

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import io.kotless.dsl.ktor.app.KotlessCall
import io.kotless.dsl.ktor.app.KotlessEngine
import io.kotless.dsl.model.*
import io.kotless.dsl.utils.Json
import io.ktor.application.Application
import io.ktor.server.engine.EngineAPI
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.util.pipeline.execute
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream


@Suppress("unused")
abstract class Kotless : RequestStreamHandler {
    private var prepared = false

    private val logger = LoggerFactory.getLogger(Kotless::class.java)

    @EngineAPI
    val engine = KotlessEngine(applicationEngineEnvironment {
        log = logger
    }).also {
        it.start()
    }

    abstract fun prepare(app: Application)

    @EngineAPI
    override fun handleRequest(input: InputStream, output: OutputStream, @Suppress("UNUSED_PARAMETER") any: Context) {
        if (!prepared) {
            prepare(engine.application)
            prepared = true
        }

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
