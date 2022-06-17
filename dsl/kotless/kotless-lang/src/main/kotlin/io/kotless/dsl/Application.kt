package io.kotless.dsl

import io.kotless.InternalAPI
import io.kotless.dsl.app.http.RoutesStorage
import io.kotless.dsl.lang.LambdaInit
import io.kotless.dsl.lang.LambdaWarming
import io.kotless.dsl.lang.event.Cloudwatch
import io.kotless.dsl.reflection.ReflectionScanner
import org.slf4j.LoggerFactory

@InternalAPI
object Application {
    private val logger = LoggerFactory.getLogger(Application::class.java)

    private var isInitialized = false

    fun init() {
        if (isInitialized) return
        logger.debug("Started initialization of Lambda")

        RoutesStorage.scan()

        executeForObjects<LambdaInit> { it.init() }

        warmup()

        logger.debug("Lambda is initialized")
        isInitialized = true
    }

    fun warmup() = executeForObjects<LambdaWarming> {
        logger.info("warmup!")
        it.warmup()
    }

    private inline fun <reified T : Any> executeForObjects(body: (T) -> Unit) {
        ReflectionScanner.objectsWithSubtype<T>().forEach {
            try {
                body(it)
            } catch (e: Throwable) {
                logger.error("Exception occurred during call of ${T::class} sequence for object ${it::class.qualifiedName}", e)
            }
        }
    }

}
