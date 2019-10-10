package io.kotless.dsl.events

import io.kotless.ScheduledEventType
import io.kotless.dsl.lang.event.Scheduled
import io.kotless.dsl.reflection.ReflectionScanner
import org.slf4j.LoggerFactory
import kotlin.math.absoluteValue
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation


internal object EventsCache {
    private val logger = LoggerFactory.getLogger(EventsCache::class.java)

    private val cache = HashMap<String, KFunction<*>>()

    private var scanned = false

    fun scan() {
        if (scanned) return

        ReflectionScanner.funcsWithAnnotation<Scheduled>().forEach { route ->
            logger.info("Found function ${route.name} for annotation ${Scheduled::class.simpleName}")
            val annotation = route.findAnnotation<Scheduled>()
            if (annotation != null) {
                val id = route.name
                val key = "${ScheduledEventType.General.prefix}-${id.hashCode().absoluteValue}"
                logger.info("Key $key")
                cache[key] = route
            }
        }

        scanned = true
    }

    operator fun get(key: String): KFunction<*>? {
        scan()
        return cache[key] ?: return null
    }

}
