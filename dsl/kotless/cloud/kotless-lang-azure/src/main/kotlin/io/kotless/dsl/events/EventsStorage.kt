package io.kotless.dsl.app.events

import io.kotless.InternalAPI
import io.kotless.dsl.lang.event.Cloudwatch
import org.slf4j.LoggerFactory
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction


@InternalAPI
internal object EventsStorage {
    private val logger = LoggerFactory.getLogger(EventsStorage::class.java)

    private val cache = HashMap<String, KFunction<*>>()

    private var scanned = false

    private fun scan() {
        if (scanned) return

        for ((ids, method, _) in EventsReflectionScanner.getEvents()) {
            val kFunc = method.kotlinFunction!!
            for (id in ids) {
                cache[id] = kFunc
                logger.debug("Saved with key $id function ${kFunc.name} for annotation ${Cloudwatch::class.simpleName}")
            }
        }

        scanned = true
    }

    operator fun get(key: String): KFunction<*>? {
        scan()
        return cache[key] ?: return null
    }
}
