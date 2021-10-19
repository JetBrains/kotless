package io.kotless.dsl.ktor

import com.microsoft.azure.functions.*
import com.microsoft.azure.functions.annotation.*
import io.kotless.InternalAPI
import io.kotless.ScheduledEventType
import io.kotless.dsl.cloud.azure.AzureRequestHandler
import io.kotless.dsl.cloud.azure.model.toRequest
import io.kotless.dsl.ktor.app.KotlessCall
import io.kotless.dsl.ktor.app.KotlessEngine
import io.kotless.dsl.ktor.lang.LambdaWarming
import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.*


/**
 * Entrypoint of Kotless application written with Ktor DSL.
 *
 * Override [prepare] method and setup your application
 */
@Suppress("unused")
@InternalAPI
abstract class KotlessAzure : AzureRequestHandler {
    companion object {
        private val logger = LoggerFactory.getLogger(KotlessAzure::class.java)

        private var prepared = false

        @EngineAPI
        val engine = KotlessEngine(applicationEngineEnvironment {
            log = logger
        }).also {
            it.start()
        }
    }

    abstract fun prepare(app: Application)

    @InternalAPI
    @EngineAPI
    override fun run(
        @HttpTrigger(
            name = "req",
            methods = [HttpMethod.GET, HttpMethod.POST],
            authLevel = AuthorizationLevel.FUNCTION
        ) request: HttpRequestMessage<Optional<String>>, context: ExecutionContext
    ): HttpResponseMessage {
        if (!prepared) {
            prepare(engine.application)
            prepared = true
        }

        val response = try {
            runBlocking {
                val myRequest = request.toRequest()

                logger.debug("Started handling request")
                logger.trace("Request is {}", myRequest.body?.string ?: "")

                val call = KotlessCall(engine.application, myRequest)

                engine.pipeline.execute(call)

                call.response.toHttp()
            }
        } catch (e: Throwable) {
            logger.error("Error occurred during handle of request and was not caught", e)
            null
        } ?: return request
            .createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
            .build()
        val outputResponse = request
            .createResponseBuilder(HttpStatus.valueOf(response.statusCode))
            .body(response.body)
        response.headers.forEach {
            outputResponse.header(it.key, it.value)
        }
        logger.info("Ended handling request")
        return outputResponse.build()
    }

    @OptIn(InternalAPI::class, EngineAPI::class)
    override fun timer(@TimerTrigger(name = "timer", schedule = "* * * * * *") timer: String, context: ExecutionContext) {
        logger.trace("Executing warmup sequence")
        engine.environment.monitor.raise(LambdaWarming, engine.application)
        logger.trace("Warmup sequence executed")

    }
}
