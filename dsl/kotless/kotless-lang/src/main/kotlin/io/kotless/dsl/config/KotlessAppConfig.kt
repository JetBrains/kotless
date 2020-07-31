package io.kotless.dsl.config

import io.kotless.InternalAPI

@InternalAPI
object KotlessAppConfig {
    const val PACKAGE_ENV_NAME = "KOTLESS_PACKAGES"

    fun packages(value: String) = value.split(",").map { it.trim() }.toSet()

    val packages by lazy { System.getenv(PACKAGE_ENV_NAME)?.split(",") ?: error("No KOTLESS_PACKAGES discovered!") }
}
