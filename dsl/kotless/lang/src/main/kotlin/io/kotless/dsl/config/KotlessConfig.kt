package io.kotless.dsl.config

internal object KotlessConfig {
    private const val PACKAGE_ENV_NAME = "KOTLESS_PACKAGES"

    val packages by lazy { System.getenv(PACKAGE_ENV_NAME)?.split(",") ?: error("No KOTLESS_PACKAGES discovered!") }
}
