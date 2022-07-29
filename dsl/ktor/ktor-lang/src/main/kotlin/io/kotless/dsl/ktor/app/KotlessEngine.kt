package io.kotless.dsl.ktor.app

import io.ktor.server.engine.*

/**
 * Kotless implementation of Ktor engine.
 * Optimized for serverless use-case.
 */
class KotlessEngine(environment: ApplicationEngineEnvironment) : BaseApplicationEngine(environment) {
    override fun start(wait: Boolean): ApplicationEngine {
        environment.start()
        return this
    }

    override fun stop(gracePeriodMillis: Long, timeoutMillis: Long) {
        environment.stop()
    }
}
