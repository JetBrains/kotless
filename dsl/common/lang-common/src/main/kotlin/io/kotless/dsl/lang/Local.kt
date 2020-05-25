package io.kotless.dsl.lang

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import io.kotless.*
import io.kotless.Constants.LocalStack
import java.lang.System.getenv


/**
 * Sets up access and endpoint configuration for Kotless local start.
 *
 * It will do nothing and cost nothing, when app is deployed to cloud.
 *
 * **Note**: you should use it for all clients, that will be used by app during local start. Most likely - just for all clients
 *
 * **Example**: *AmazonDynamoDBClientBuilder.standard().withKotlessLocal(AwsResource.DynamoDB).build()*
 */
@Suppress("unused")
@OptIn(InternalAPI::class)
fun <E, T: AwsClientBuilder<T, E>> AwsClientBuilder<T, E>.withKotlessLocal(resource: AwsResource): AwsClientBuilder<T, E> {
    if (getenv(LocalStack.enabled)?.toBoolean() == true) {
        setEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(getenv(LocalStack.url(resource)), getenv(LocalStack.region(resource))))
        credentials = AWSStaticCredentialsProvider(BasicAWSCredentials(getenv(LocalStack.accessKey), getenv(LocalStack.secretKey)))
    }
    return this
}
