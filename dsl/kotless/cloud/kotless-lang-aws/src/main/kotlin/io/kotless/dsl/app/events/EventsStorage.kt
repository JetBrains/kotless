package io.kotless.dsl.app.events

import io.kotless.InternalAPI
import io.kotless.dsl.lang.event.Scheduled
import org.slf4j.LoggerFactory
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
            val kFunc = method.kotlinFunction!!
            for (id in ids) {
                cache.add(id to kFunc)
                logger.debug("Saved with key $id function ${kFunc.name} for annotation ${Scheduled::class.simpleName}")
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
