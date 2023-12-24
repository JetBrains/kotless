package io.kotless.graal.runtime

import com.amazonaws.services.lambda.runtime.Context
import io.kotless.graal.runtime.LambdaEnvironment.DEADLINE_HEADER_NAME
import io.kotless.graal.runtime.LambdaEnvironment.INVOKED_FUNCTION_ARN
import io.kotless.graal.runtime.LambdaEnvironment.REQUEST_HEADER_NAME
import io.kotless.graal.runtime.client.LambdaHTTPClient
import io.kotless.graal.runtime.objects.AwsLambdaInvocation
import io.kotless.graal.runtime.objects.LambdaContext
import java.net.http.HttpResponse
import java.util.logging.Logger

val log: Logger = Logger.getLogger("Kotlin Custom Runtime")

fun main() {
    log.info("Init Kotlin GraalVM Runtime.")
    while (true) {
        initLambdaInvocation { context, awsLambdaInvocation ->
            Adapter.handleLambdaInvocation(context, awsLambdaInvocation)
        }
    }
}

private fun initLambdaInvocation(handle: (context: Context, apiGatewayProxyRequest: String) -> Unit) {
    log.info("Create lambda invocation..")
    try {
        val (context, apiGatewayProxyRequest) = createLambdaInvocation()
        log.info("Get the invocation. Request ID: ${context.awsRequestId}")
        handle(context, apiGatewayProxyRequest)
    } catch (t: Throwable) {
        t.printStackTrace()
        LambdaHTTPClient.postInitError(t.message)
    }
}

private fun createLambdaInvocation(): AwsLambdaInvocation {
    val response = LambdaHTTPClient.init()
    val apiGatewayProxyRequest = response.body()
    val context = getContext(response)
    return AwsLambdaInvocation(context, apiGatewayProxyRequest)
}

private fun getContext(response: HttpResponse<String>): LambdaContext {
    val requestId = response.headers().firstValue(REQUEST_HEADER_NAME).orElse(null)
        ?: error("Header: $REQUEST_HEADER_NAME was not found")
    val deadLineTime = response.headers().firstValue(DEADLINE_HEADER_NAME).orElse("0").toLong()
    val invokedFuncArn = response.headers().firstValue(INVOKED_FUNCTION_ARN).orElse(null)
    return LambdaContext(requestId, deadLineTime, invokedFuncArn)
}
