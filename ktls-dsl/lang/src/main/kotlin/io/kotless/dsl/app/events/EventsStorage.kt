package io.kotless.dsl.app.events

import io.kotless.ScheduledEventType
import io.kotless.dsl.lang.event.Scheduled
import io.kotless.dsl.reflection.ReflectionScanner
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import kotlin.math.absoluteValue
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.kotlinFunction


internal object EventsStorage {
    private val logger = LoggerFactory.getLogger(EventsStorage::class.java)

    private val cache = HashMap<String, KFunction<*>>()

    private var scanned = false

    fun scan() {
        if (scanned) return

        ReflectionScanner.methodsWithAnnotation<Scheduled>().forEach { method ->
            logger.info("Found function ${method.name} for annotation ${Scheduled::class.simpleName}")
            val kFunc = method.kotlinFunction
            val annotation = kFunc?.findAnnotation<Scheduled>()
            if (annotation != null) {
                if (annotation.id.isNotBlank()) {
                    cache[annotation.id] = kFunc
                } else {
                    val ids = method.possibleNames()
                    val keys = ids.map { "${ScheduledEventType.General.prefix}-${it.hashCode().absoluteValue}" }
                    for (key in keys) {
                        logger.info("Key $key")
                        cache[key] = kFunc
                    }
                }
            }
        }

        scanned = true
    }

    operator fun get(key: String): KFunction<*>? {
        scan()
        return cache[key] ?: return null
    }

    private fun Method.possibleNames(): Set<String> {
        val klass = declaringClass.kotlin.qualifiedName!!
        return setOf("$klass.$name", "${klass.substringBeforeLast(".")}.$name")
    }

}
