package io.kotless.dsl

import com.amazonaws.services.lambda.runtime.Context
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



        } catch (e: Throwable) {
            logger.error("Error occurred during handle of request and was not caught", e)
        }

        logger.info("Ended handling request")
    }
}
