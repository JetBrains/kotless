package io.kotless.graal.aws.runtime

internal object LambdaRouters {
    private const val RUNTIME_DATE = "2018-06-01"
    private val LOCAL_PORT = System.getenv("AWS_API_GATEWAY_PORT")?.toIntOrNull() ?: 8080
    private val RUNTIME_API = System.getenv("AWS_LAMBDA_RUNTIME_API") ?: "localhost:$LOCAL_PORT"
    private val RUNTIME_BASE_URL = "http://$RUNTIME_API/$RUNTIME_DATE/runtime"

    // Lambda API
    val RUNTIME_INITIALIZE_ERROR = "$RUNTIME_BASE_URL/init/error"
    val INVOKE_NEXT = "$RUNTIME_BASE_URL/invocation/next"
    fun getInvocationResponse(requestId: String) = "$RUNTIME_BASE_URL/invocation/$requestId/response"
    fun getInvocationError(requestId: String) = "$RUNTIME_BASE_URL/invocation/$requestId/error"
}

/**
 * Runtime environment
 * https://docs.aws.amazon.com/lambda/latest/dg/configuration-envvars.html#configuration-envvars-runtime
 */
internal object LambdaEnvironment {
    const val REQUEST_HEADER_NAME = "Lambda-Runtime-Aws-Request-Id"
    const val DEADLINE_HEADER_NAME = "Lambda-Runtime-Deadline-Ms"
    const val INVOKED_FUNCTION_ARN = "Lambda-Runtime-Invoked-Function-Arn"
    val HANDLER_CLASS: String = System.getenv("_HANDLER") ?: error("No handler method provided")
    val LAMBDA_TASK_ROOT: String = System.getenv("LAMBDA_TASK_ROOT") ?: ""

    // Additional
    val FUNCTION_VERSION: String = System.getenv("AWS_LAMBDA_FUNCTION_VERSION") ?: "-1"
    val LOG_GROUP_NAME: String = System.getenv("AWS_LAMBDA_LOG_GROUP_NAME") ?: "local"
    val MEMORY_LIMIT: Int = System.getenv("AWS_LAMBDA_FUNCTION_MEMORY_SIZE")?.toIntOrNull() ?: 1024
    val FUNCTION_NAME: String = System.getenv("AWS_LAMBDA_FUNCTION_NAME") ?: ""
    val LOG_STREAM_NAME: String = System.getenv("AWS_LAMBDA_LOG_STREAM_NAME") ?: "local"

}
