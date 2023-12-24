package io.kotless.graal.aws.runtime

import com.amazonaws.services.lambda.runtime.Context
import io.kotless.graal.aws.runtime.client.LambdaHTTPClient
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream

/**
 * Example class.
 * That code will be replaced on client code function.
 * See example in: [io.kotless.graal.plugin.tasks.GenerateAdapter]
 */
@Suppress("UNREACHABLE_CODE", "UNUSED_VARIABLE")
object Adapter {

    private val log = LoggerFactory.getLogger(Adapter::class.java)

    fun handleLambdaInvocation(context: Context, apiGatewayProxyRequest: String) {
        try {
            val input = apiGatewayProxyRequest.byteInputStream()
            val output = ByteArrayOutputStream()
            // here goes call
            error("Initial Adapter should never be called")
            // here goes call
            LambdaHTTPClient.invoke(context.awsRequestId, output.toByteArray())
        } catch (t: Throwable) {
            log.error("Invocation error", t)
            LambdaHTTPClient.postInvokeError(context.awsRequestId, t.message)
        }
    }
}
