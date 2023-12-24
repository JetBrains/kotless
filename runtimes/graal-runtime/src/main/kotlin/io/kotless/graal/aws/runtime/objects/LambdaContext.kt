package io.kotless.graal.aws.runtime.objects

import com.amazonaws.services.lambda.runtime.ClientContext
import com.amazonaws.services.lambda.runtime.CognitoIdentity
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import io.kotless.graal.aws.runtime.LambdaEnvironment
import java.util.*

data class LambdaContext(
    private val requestId: String,
    private val deadlineTime: Long,
    private val invokedFuncArn: String?
) : Context {
    override fun getAwsRequestId(): String = requestId
    override fun getInvokedFunctionArn(): String? = invokedFuncArn
    override fun getLogStreamName(): String = LambdaEnvironment.LOG_STREAM_NAME
    override fun getMemoryLimitInMB(): Int = LambdaEnvironment.MEMORY_LIMIT
    override fun getLogGroupName(): String = LambdaEnvironment.LOG_GROUP_NAME
    override fun getFunctionVersion(): String = LambdaEnvironment.FUNCTION_VERSION
    override fun getFunctionName(): String = LambdaEnvironment.FUNCTION_NAME
    override fun getRemainingTimeInMillis(): Int = (Date().time - deadlineTime).toInt()

    override fun getLogger(): LambdaLogger = object : LambdaLogger {
        override fun log(message: String?) = println(message)
        override fun log(message: ByteArray?) = println(message)
    }

    /**
     * For invocations from the AWS Mobile SDK, data about the Amazon Cognito identity provider.
     * Header: Lambda-Runtime-Cognito-Identity
     */
    override fun getIdentity(): CognitoIdentity? = null

    /**
     * For invocations from the AWS Mobile SDK, data about the client application and device.
     * Header: Lambda-Runtime-Client-Context
     */
    override fun getClientContext(): ClientContext? = null
}
