package io.kotless.dsl.spring

import com.amazonaws.serverless.proxy.model.AwsProxyRequest
import com.amazonaws.serverless.proxy.model.AwsProxyResponse
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import java.io.InputStream
import java.io.OutputStream
import kotlin.reflect.KClass

abstract class Kotless : RequestStreamHandler {
    abstract val bootKlass: KClass<*>

    companion object {
        private var prepared: Boolean = false

        private var handler: SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse>? = null
    }

    override fun handleRequest(inputStream: InputStream, outputStream: OutputStream, context: Context) {
        if (!prepared) {
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(bootKlass.java)
        }

        handler?.proxyStream(inputStream, outputStream, context)
    }
}
