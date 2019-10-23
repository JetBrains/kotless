package io.kotless.dsl.config

object KotlessAppConfig {
    const val PACKAGE_ENV_NAME = "KOTLESS_PACKAGES"

    fun packages(value: String) = value.split(",").map { it.trim() }.toSet()

    val packages by lazy { System.getenv(PACKAGE_ENV_NAME)?.split(",") ?: error("No KOTLESS_PACKAGES discovered!") }
}
