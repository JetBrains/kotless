package io.kotless.graal.aws.runtime.objects

data class AwsLambdaInvocation(
    val context: LambdaContext,
    val apiGatewayProxyRequest: String
)
