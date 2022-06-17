package io.kotless.dsl

import com.microsoft.azure.functions.*
import com.microsoft.azure.functions.annotation.*
import io.kotless.*
import io.kotless.HttpMethod
import io.kotless.dsl.app.events.EventsStorage
import io.kotless.dsl.app.http.RouteKey
import io.kotless.dsl.app.http.RoutesDispatcher
import io.kotless.dsl.cloud.azure.AzureRequestHandler
import io.kotless.dsl.cloud.azure.model.toRequest
import io.kotless.dsl.reflection.FunctionCaller
import org.slf4j.LoggerFactory
import java.util.*

@InternalAPI
class HandlerAzure : AzureRequestHandler {
    companion object {
        private val logger = LoggerFactory.getLogger(HandlerAzure::class.java)
    }

    override fun run(
        @HttpTrigger(
            name = "req",
            methods = [com.microsoft.azure.functions.HttpMethod.GET, com.microsoft.azure.functions.HttpMethod.POST],
            authLevel = AuthorizationLevel.FUNCTION
        ) request: HttpRequestMessage<Optional<String>>, context: ExecutionContext
    ): HttpResponseMessage {
        val response = try {
            val myRequest = request.toRequest()

            logger.debug("Started handling request")
            logger.trace("Request is {}", myRequest.body?.string ?: "")

            Application.init()

            logger.info(myRequest.path)
            val resourceKey = RouteKey(HttpMethod.valueOf(request.httpMethod.name), myRequest.path)

            RoutesDispatcher.dispatch(myRequest, resourceKey)
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

    override fun timer(@TimerTrigger(name = "timer", schedule = "* * * * * *") timer: String, context: ExecutionContext) {
        val resource = context.functionName
        when {
            resource.contains(CloudwatchEventType.Autowarm.prefix) -> {
                logger.trace("Executing warmup sequence")
                Application.warmup()
                logger.trace("Warmup sequence executed")
            }
            resource.contains(CloudwatchEventType.General.prefix) -> {
                val key = resource.substring(resource.lastIndexOf(CloudwatchEventType.General.prefix))

                logger.trace("Executing scheduled lambda with key $key")
                EventsStorage[key]?.let { FunctionCaller.call(it, emptyMap()) }
                logger.trace("Scheduled lambda with key $key was executed")
            }
        }
    }
}
