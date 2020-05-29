package io.kotless.plugin.gradle.utils

internal object AWSUtils {
    val fakeCredentials = mapOf(
        "AWS_SECRET_ACCESS_KEY" to "fake",
        "AWS_ACCESS_KEY_ID" to "fake"
    )
}
