package io.kotless.dsl.app

import io.ktor.server.engine.*
import java.util.concurrent.TimeUnit

@EngineAPI
class KotlessEngine(environment: ApplicationEngineEnvironment) : BaseApplicationEngine(environment) {
    override fun start(wait: Boolean): ApplicationEngine {
        environment.start()
        return this
    }

    override fun stop(gracePeriod: Long, timeout: Long, timeUnit: TimeUnit) {
        environment.stop()
    }
}
