package io.kotless.dsl

import io.kotless.dsl.conversion.ConversionService
import io.kotless.dsl.dispatcher.RoutesCache
import io.kotless.dsl.lang.LambdaInit
import io.kotless.dsl.lang.LambdaWarming
import io.kotless.dsl.reflection.ReflectionScanner
import org.slf4j.LoggerFactory

internal object Application {
    private val logger = LoggerFactory.getLogger(io.kotless.dsl.Application::class.java)

    private var isInitialized = false


    fun init() {
        if (io.kotless.dsl.Application.isInitialized) return
        io.kotless.dsl.Application.logger.info("Started initialization of Lambda")

        RoutesCache.scan()

        io.kotless.dsl.Application.initConversions()

        io.kotless.dsl.Application.startInitSequence()
        io.kotless.dsl.Application.startWarmingSequence()

        io.kotless.dsl.Application.logger.info("Lambda is initialized")
        io.kotless.dsl.Application.isInitialized = true
    }

    private fun initConversions() {
        ReflectionScanner.objectsWithSubtype<ConversionService>().forEach {
            ConversionService.register(it)
        }
    }

    private fun startInitSequence() {
        ReflectionScanner.objectsWithSubtype<LambdaInit>().forEach {
            try {
                it.init()
            } catch (e: Throwable) {
                io.kotless.dsl.Application.logger.error("Exception occurred during call of initializing sequence function ${it::class.qualifiedName}", e)
            }
        }
    }

    /** Start warming up sequence */
    fun startWarmingSequence() {
        ReflectionScanner.objectsWithSubtype<LambdaWarming>().forEach {
            try {
                it.warmup()
            } catch (e: Throwable) {
                io.kotless.dsl.Application.logger.error("Exception occurred during call of warming sequence function ${it::class.qualifiedName}", e)
            }
        }
    }
}
