package io.kotless.dsl.ktor

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import io.kotless.InternalAPI
import io.kotless.dsl.cloud.aws.CloudWatch
import io.kotless.dsl.cloud.aws.model.AwsHttpRequest
import io.kotless.dsl.ktor.app.*
import io.kotless.dsl.ktor.lang.LambdaWarming
import io.kotless.dsl.model.AwsEvent
import io.kotless.dsl.model.HttpResponse
import io.kotless.dsl.model.events.*
import io.kotless.dsl.utils.JSON
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream


/**
 * Entrypoint of Kotless application written with Ktor DSL.
 *
 * Override [prepare] method and setup your application
 */
@Suppress("unused")
abstract class KotlessAWS : RequestStreamHandler {
    companion object {
        private val logger = LoggerFactory.getLogger(KotlessAWS::class.java)

        private var prepared = false

        fun registerAwsEvent(eventSource: String, deserialization: (String) -> AwsEventInformation) {
            AwsEventInformation.eventSerializers.put(eventSource, deserialization)
        }

        @EngineAPI
        val engine = KotlessEngine(applicationEngineEnvironment {
            log = logger
        }).also {
            it.start()
        }

        fun Route.s3(bucket: String, event: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
            return route(
                "$bucket/${event.replace(":", "/").replace("*", "{method}").lowercase()}",
                HttpMethod("aws:s3")
            ) { handle(body) }
        }

        fun Route.sqs(queueArn: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
            return route(queueArn.lowercase().replace(":", "/"), HttpMethod("aws:sqs")) { handle(body) }
        }
    }

    init {
        registerAwsEvent("aws:s3", S3EventInformation::deserialize)
        registerAwsEvent("aws:sqs", SQSEventInformation::deserialize)
    }

    abstract fun prepare(app: Application)

    @OptIn(InternalAPI::class, EngineAPI::class)
    override fun handleRequest(input: InputStream, output: OutputStream, @Suppress("UNUSED_PARAMETER") any: Context?) {
        if (!prepared) {
            prepare(engine.application)
            prepared = true
        }

        val response = try {
            runBlocking {
                val json = input.bufferedReader().use { it.readText() }

                logger.info("Started handling request")
                logger.debug("Request is {}", json)
                logger.info(json)
                if (json.contains("\"aws:s3\"") || json.contains("\"aws:sqs\"")) {
                    logger.info("Request is S3 event!")
                    val event = JSON.parse(AwsEvent.serializer(), json)
                    val call = AwsEventCall(engine.application, event.records.first())
                    logger.info("Method of s3Event: ${call.request.local.uri}")
                    engine.pipeline.execute(call)

                    return@runBlocking call.response.toHttp()
                }

                if (json.contains("Scheduled Event")) {
                    val event = JSON.parse(CloudWatch.serializer(), json)
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

                val request = JSON.parse(AwsHttpRequest.serializer(), json)
                val call = KotlessCall(engine.application, request.toRequest())

                engine.pipeline.execute(call)

                call.response.toHttp()
            }
        } catch (e: Throwable) {
            logger.error("Error occurred during handle of request and was not caught", e)
            null
        }

        if (response != null) {
            output.write(JSON.bytes(HttpResponse.serializer(), response))
        } else {
            logger.info("Got null response")
        }

        logger.info("Ended handling request")
    }
}
