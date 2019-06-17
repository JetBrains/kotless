package io.kotless.dsl.utils

import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder

internal fun <T> tryRun(body: () -> T): T? = try {
    body()
} catch (e: Throwable) {
    null
}

internal fun ConfigurationBuilder.forPackages(values: Collection<String>) = apply { values.forEach { addUrls(ClasspathHelper.forPackage(it)) } }
