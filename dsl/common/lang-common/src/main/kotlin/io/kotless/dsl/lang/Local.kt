package io.kotless.dsl.lang

import io.kotless.AwsResource

/**
 * Returns url and region that should be used during local run
 * Will do nothing during run in cloud
 */
fun <T> T.withLocalEndpoint(resource: AwsResource, body: T.(url: String, region: String) -> Unit): T {
    if (System.getenv("LOCALSTACK_ENABLED")?.toBoolean() == true) {
        val url = System.getenv("LOCALSTACK_${resource.prefix.toUpperCase()}_URL")
        val region = System.getenv("LOCALSTACK_${resource.prefix.toUpperCase()}_REGION")
        body(url, region)
    }
    return this
}

/**
 * Returns credentials that should be used during local run
 * Will do nothing during run in cloud
 */
fun <T> T.withLocalCredentials(resource: AwsResource, body: T.(accessKey: String, secretKey: String) -> Unit): T {
    if (System.getenv("LOCALSTACK_ENABLED")?.toBoolean() == true) {
        val accessKey = System.getenv("LOCALSTACK_ACCESSKEY")
        val secretKey = System.getenv("LOCALSTACK_SECRETKEY")
        body(accessKey, secretKey)
    }
    return this
}
