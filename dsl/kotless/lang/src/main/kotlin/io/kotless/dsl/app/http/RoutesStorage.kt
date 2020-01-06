package io.kotless.dsl.app.http

import io.kotless.*
import io.kotless.dsl.lang.http.*
import io.kotless.dsl.reflection.ReflectionScanner
import org.slf4j.LoggerFactory
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation

@InternalAPI
internal object RoutesStorage {
    data class Descriptor(val func: KFunction<*>, val mime: MimeType)

    private val logger = LoggerFactory.getLogger(RoutesStorage::class.java)

    private val cache = HashMap<RouteKey, Descriptor>()

    private var scanned = false

    fun scan() {
        if (scanned) return

        scanFor<Get>()
        scanFor<Post>()
        scanFor<Put>()
        scanFor<Patch>()
        scanFor<Delete>()
        scanFor<Head>()
        scanFor<Options>()

        scanned = true
    }

    private inline fun <reified T : Annotation> scanFor() {
        ReflectionScanner.funcsWithAnnotation<T>().forEach { route ->
            logger.debug("Found function ${route.name} for annotation ${T::class.simpleName}")
            val annotation = route.findAnnotation<T>()
            if (annotation != null) {
                val key = annotation.toRouteKey()
                if (cache.containsKey(key)) logger.error("Found overriding route for $key. Previous route is ${cache.getValue(key).func.name}, new ${route.name}")

                cache[key] = Descriptor(route, annotation.refMime)
                logger.debug("Saved with key $key function ${route.name} for annotation ${T::class.simpleName}")
            }
        }
    }

    operator fun get(key: RouteKey): Descriptor? {
        scan()
        return cache[key] ?: return null
    }

    private fun Annotation.toRouteKey(): RouteKey = when (this) {
        is Get -> RouteKey(HttpMethod.GET, path)
        is Post -> RouteKey(HttpMethod.POST, path)
        is Put -> RouteKey(HttpMethod.PUT, path)
        is Patch -> RouteKey(HttpMethod.PATCH, path)
        is Delete -> RouteKey(HttpMethod.DELETE, path)
        is Head -> RouteKey(HttpMethod.HEAD, path)
        is Options -> RouteKey(HttpMethod.OPTIONS, path)
        else -> error("Unsupported annotation $this")
    }

    private val Annotation.refMime
        get() = when (this) {
            is Get -> mime
            is Post -> mime
            is Put -> mime
            is Patch -> mime
            is Delete -> mime
            is Head -> mime
            is Options -> mime
            else -> error("Unsupported annotation $this")
        }
}
