package io.kotless.dsl

import io.kotless.InternalAPI
import io.kotless.dsl.app.http.RoutesStorage
import io.kotless.dsl.lang.LambdaInit
import io.kotless.dsl.lang.LambdaWarming
import io.kotless.dsl.reflection.ReflectionScanner
import io.reflekt.Reflekt
import org.slf4j.LoggerFactory

@InternalAPI
object Application {
    private val logger = LoggerFactory.getLogger(Application::class.java)

    private var isInitialized = false

    fun init() {
        if (isInitialized) return
        logger.debug("Started initialization of Lambda")

        println("SCANNING")
        RoutesStorage.scan()

        Reflekt.objects().withSupertype<LambdaInit>().toList().forEach {
            it.init()
        }

        warmup()

        logger.debug("Lambda is initialized")
        isInitialized = true
    }

    fun warmup() {
        Reflekt.objects().withSupertype<LambdaWarming>().toList().forEach {
            try {
                it.warmup()
            } catch (e: Throwable) {
                logger.error("Exception occurred during call of ${LambdaWarming::class} sequence for object ${it::class.qualifiedName}", e)
            }
        }
    }

}
