package io.kotless.dsl.app

import io.ktor.application.Application
import io.ktor.application.ApplicationEvents
import io.ktor.config.ApplicationConfig
import io.ktor.config.MapApplicationConfig
import io.ktor.server.engine.ApplicationEngineEnvironment
import io.ktor.server.engine.EngineConnectorConfig
import io.ktor.util.KtorExperimentalAPI
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


class KotlessEnvironment(override val application: Application) : ApplicationEngineEnvironment {
    override val connectors: List<EngineConnectorConfig> = emptyList()

    override fun start() {}

    override fun stop() {}

    override val classLoader: ClassLoader = this::class.java.classLoader

    @KtorExperimentalAPI
    override val config: ApplicationConfig = MapApplicationConfig()

    override val log: Logger = LoggerFactory.getLogger("ApplicationKt")

    override val monitor: ApplicationEvents = ApplicationEvents()

    override val parentCoroutineContext: CoroutineContext = EmptyCoroutineContext

    @KtorExperimentalAPI
    override val rootPath: String = "/"
}

