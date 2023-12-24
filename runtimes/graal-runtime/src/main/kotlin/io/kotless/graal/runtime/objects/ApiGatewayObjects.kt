package io.kotless.graal.runtime.objects

data class AwsLambdaInvocation(
    val context: LambdaContext,
    val apiGatewayProxyRequest: String
)
