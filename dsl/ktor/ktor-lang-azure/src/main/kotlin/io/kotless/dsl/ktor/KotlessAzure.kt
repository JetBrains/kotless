package io.kotless.dsl.ktor

import com.microsoft.azure.functions.*
import com.microsoft.azure.functions.annotation.*
import io.kotless.InternalAPI
import io.kotless.dsl.AzureRequestHandler
import io.kotless.dsl.ktor.app.KotlessCall
import io.kotless.dsl.ktor.app.KotlessEngine
import io.kotless.dsl.ktor.lang.LambdaWarming
import io.kotless.dsl.model.CloudWatch
import io.kotless.dsl.model.HttpRequest
import io.kotless.dsl.utils.Json
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
    @FunctionName("HttpTrigger")
    override fun handleRequest(
        @HttpTrigger(
            name = "req",
            methods = [HttpMethod.GET, HttpMethod.POST],
            authLevel = AuthorizationLevel.FUNCTION
        ) request: HttpRequestMessage<Optional<String>>, context: ExecutionContext
    ): HttpResponseMessage {
        logger.info("new message")
        if (!prepared) {
            prepare(engine.application)
            prepared = true
        }

        val response = try {
            runBlocking {
                val json = if (request.body.isPresent) {
                    request.body.get()
                } else {
                    ""
                }

                logger.info("Started handling request")
                logger.debug("Request is {}", json)

                if (json.contains("Scheduled Event")) {
                    val event = Json.parse(CloudWatch.serializer(), json)
                    if (event.`detail-type` == "Scheduled Event" && event.source == "aws.events") {
                        logger.info("Request is Scheduled Event")
                        try {
                            engine.environment.monitor.raise(LambdaWarming, engine.application)
                        } catch (e: Throwable) {
                            logger.error("One or more of the LambdaWarming handlers thrown an exception", e)
                        }
                        return@runBlocking null
                    }
                }

                logger.info("Request is HTTP Event")

                val origPath = request.headers["x-original-path"] ?: ""

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

                val requestBody = HttpRequest(origPath, origPath, io.kotless.HttpMethod.valueOf(request.httpMethod.name), null, request.queryParameters, null, requestContext, json, false)

                val call = KotlessCall(engine.application, requestBody)

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
}
