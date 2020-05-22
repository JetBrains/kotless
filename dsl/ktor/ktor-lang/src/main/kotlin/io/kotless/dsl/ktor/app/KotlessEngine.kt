package io.kotless.dsl.ktor.app

import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.ApplicationEngineEnvironment
import io.ktor.server.engine.BaseApplicationEngine
import io.ktor.server.engine.EngineAPI

@EngineAPI
class KotlessEngine(environment: ApplicationEngineEnvironment) : BaseApplicationEngine(environment) {
    override fun start(wait: Boolean): ApplicationEngine {
        environment.start()
        return this
    }

    override fun stop(gracePeriodMillis: Long, timeoutMillis: Long) {
        environment.stop()
    }
}
