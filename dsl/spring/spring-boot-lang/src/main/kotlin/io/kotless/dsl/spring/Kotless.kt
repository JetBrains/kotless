package io.kotless.dsl.spring

import com.amazonaws.serverless.proxy.model.AwsProxyRequest
import com.amazonaws.serverless.proxy.model.AwsProxyResponse
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import io.kotless.InternalAPI
import io.kotless.dsl.cloud.aws.CloudWatch
import io.kotless.dsl.utils.JSON
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Entrypoint of Kotless application written with Spring Boot DSL.
 *
 * Override [bootKlass] field and initialize it with class of your `@SpringBootApplication`
 */
abstract class Kotless : RequestStreamHandler {
    abstract val bootKlass: KClass<*>

    companion object {
        private val logger = LoggerFactory.getLogger(Kotless::class.java)

        private val classToHandler = ConcurrentHashMap<KClass<*>, SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse>>()

        fun getHandler(bootKlass: KClass<*>): SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> {
            return classToHandler.computeIfAbsent(bootKlass) { SpringBootLambdaContainerHandler.getAwsProxyHandler(bootKlass.java) }
        }
    }

    @InternalAPI
    override fun handleRequest(input: InputStream, output: OutputStream, context: Context) {
        val handler = getHandler(bootKlass)

        val json = input.bufferedReader().use { it.readText() }

        logger.debug("Started handling request")
        logger.trace("Request is {}", json)

        if (json.contains("Scheduled Event")) {
            val event = JSON.parse(CloudWatch.serializer(), json)
            if (event.`detail-type` == "Scheduled Event" && event.source == "aws.events") {
                logger.debug("Request is Scheduled Event")
                logger.debug("Nothing to do during warming")
                return
            }
        }

        logger.debug("Request is HTTP Event")

        handler?.proxyStream(json.byteInputStream(), output, context)
    }
}
