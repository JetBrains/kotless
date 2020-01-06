package io.kotless.dsl.lang

import io.kotless.*

/**
 * Returns url and region that should be used during local run
 * Will do nothing during run in cloud
 */
@UseExperimental(InternalAPI::class)
fun <T> T.withLocalEndpoint(resource: AwsResource, body: T.(url: String, region: String) -> Unit): T {
    if (System.getenv(Constants.LocalStack.enabled)?.toBoolean() == true) {
        val url = System.getenv(Constants.LocalStack.url(resource))
        val region = System.getenv(Constants.LocalStack.region(resource))
        body(url, region)
    }
    return this
}

/**
 * Returns credentials that should be used during local run
 * Will do nothing during run in cloud
 */
@UseExperimental(InternalAPI::class)
fun <T> T.withLocalCredentials(resource: AwsResource, body: T.(accessKey: String, secretKey: String) -> Unit): T {
    if (System.getenv(Constants.LocalStack.enabled)?.toBoolean() == true) {
        val accessKey = System.getenv(Constants.LocalStack.accessKey)
        val secretKey = System.getenv(Constants.LocalStack.secretKey)
        body(accessKey, secretKey)
    }
    return this
}
