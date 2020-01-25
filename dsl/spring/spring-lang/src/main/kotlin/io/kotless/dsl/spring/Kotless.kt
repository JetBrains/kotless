package io.kotless.dsl.spring

import com.amazonaws.serverless.proxy.model.AwsProxyRequest
import com.amazonaws.serverless.proxy.model.AwsProxyResponse
import com.amazonaws.serverless.proxy.spring.SpringLambdaContainerHandler
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import io.kotless.InternalAPI
import io.kotless.dsl.model.CloudWatch
import io.kotless.dsl.utils.Json
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream
import kotlin.reflect.KClass

abstract class Kotless : RequestStreamHandler {
    abstract val bootKlass: KClass<*>

    companion object {
        private val logger = LoggerFactory.getLogger(Kotless::class.java)

        private var prepared: Boolean = false

        private var handler: SpringLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse>? = null
    }

    @InternalAPI
    override fun handleRequest(input: InputStream, output: OutputStream, context: Context) {
        if (!prepared) {
            handler = SpringLambdaContainerHandler.getAwsProxyHandler(bootKlass.java)
        }

        val json = input.bufferedReader().use { it.readText() }

        logger.debug("Started handling request")
        logger.trace("Request is {}", json)

        if (json.contains("Scheduled Event")) {
            val event = Json.parse(CloudWatch.serializer(), json)
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
