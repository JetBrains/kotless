package io.kotless.dsl

import io.kotless.dsl.conversion.ConversionService
import io.kotless.dsl.api.RoutesCache
import io.kotless.dsl.lang.LambdaInit
import io.kotless.dsl.lang.LambdaWarming
import io.kotless.dsl.reflection.ReflectionScanner
import org.slf4j.LoggerFactory

internal object Application {
    private val logger = LoggerFactory.getLogger(Application::class.java)

    private var isInitialized = false


    fun init() {
        if (isInitialized) return
        logger.info("Started initialization of Lambda")

        RoutesCache.scan()

        initConversions()

        startInitSequence()
        startWarmingSequence()

        logger.info("Lambda is initialized")
        isInitialized = true
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
                logger.error("Exception occurred during call of initializing sequence function ${it::class.qualifiedName}", e)
            }
        }
    }

    /** Start warming up sequence */
    fun startWarmingSequence() {
        ReflectionScanner.objectsWithSubtype<LambdaWarming>().forEach {
            try {
                it.warmup()
            } catch (e: Throwable) {
                logger.error("Exception occurred during call of warming sequence function ${it::class.qualifiedName}", e)
            }
        }
    }
}
