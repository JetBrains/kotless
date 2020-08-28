package com.kotlin.aws.runtime

import io.kotless.examples.Server
import com.kotlin.aws.runtime.client.LambdaHTTPClient
import java.io.ByteArrayOutputStream
import io.ktor.server.engine.*

val server = io.kotless.examples.Server()

@OptIn(EngineAPI::class)
object Adapter {
    fun handleLambdaInvocation(requestId: String, apiGatewayProxyRequest: String) {
        try {
            val input = apiGatewayProxyRequest.byteInputStream()
            val output = ByteArrayOutputStream()

            server.handleRequest(input, output, null)

            LambdaHTTPClient.invoke(requestId, output.toByteArray())
        } catch (t: Throwable) {
            t.printStackTrace()
            LambdaHTTPClient.postInvokeError(requestId, t.message)
        }
    }
}