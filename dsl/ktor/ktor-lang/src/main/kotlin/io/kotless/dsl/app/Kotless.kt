package io.kotless.dsl.app

import io.ktor.server.engine.*

@EngineAPI
object Kotless : ApplicationEngineFactory<KotlessEngine, KotlessEngine.Configuration> {
    override fun create(environment: ApplicationEngineEnvironment, configure: KotlessEngine.Configuration.() -> Unit): KotlessEngine {
        return KotlessEngine(environment)
    }
}
