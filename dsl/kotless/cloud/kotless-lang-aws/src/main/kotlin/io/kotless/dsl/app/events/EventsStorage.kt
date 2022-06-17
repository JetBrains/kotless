package io.kotless.dsl.app.events

import io.kotless.InternalAPI
import io.kotless.dsl.lang.event.Cloudwatch
import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction


@InternalAPI
internal object EventsStorage {
    private val logger = LoggerFactory.getLogger(EventsStorage::class.java)

    private val cache = mutableListOf<Pair<AwsEventKey, KFunction<*>>>()

    private var scanned = false

    private fun scan() {
        if (scanned) return

        for ((ids, method, _) in EventsReflectionScanner.getEvents()) {
            for (id in ids) {
                cache.add(id to method)
                logger.debug("Saved with key ${id.key} function ${method.name}")
            }
        }

        scanned = true
    }

    operator fun get(key: AwsEventKey): KFunction<*>? {
        scan()
        return cache.firstOrNull { it.first.cover(key) }?.second ?: return null
    }

    fun getAll(key: AwsEventKey): List<KFunction<*>> {
        scan()
        return cache.filter { it.first.cover(key) }.map { it.second }
    }


}
