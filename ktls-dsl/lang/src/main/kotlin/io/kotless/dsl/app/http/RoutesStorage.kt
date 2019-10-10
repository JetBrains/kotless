package io.kotless.dsl.app.http

import io.kotless.*
import io.kotless.dsl.lang.http.Get
import io.kotless.dsl.lang.http.Post
import io.kotless.dsl.reflection.ReflectionScanner
import org.slf4j.LoggerFactory
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation

internal object RoutesStorage {
    data class Descriptor(val func: KFunction<*>, val mime: MimeType)

    private val logger = LoggerFactory.getLogger(RoutesStorage::class.java)

    private val cache = HashMap<RouteKey, Descriptor>()

    private var scanned = false

    fun scan() {
        if (scanned) return

        scanFor<Get>()
        scanFor<Post>()

        scanned = true
    }

    private inline fun <reified T : Annotation> scanFor() {
        ReflectionScanner.funcsWithAnnotation<T>().forEach { route ->
            logger.debug("Found function ${route.name} for annotation ${T::class.simpleName}")
            val annotation = route.findAnnotation<T>()
            if (annotation != null) {
                val key = RouteKey(annotation.refMethod, annotation.refPath)
                if (key !in cache) logger.error("Found overriding route for $key. Previous route is ${cache.getValue(key).func.name}, new ${route.name}")

                cache[key] = Descriptor(route, annotation.refMime)
                logger.debug("Saved with key $key function ${route.name} for annotation ${T::class.simpleName}")
            }
        }
    }

    operator fun get(key: RouteKey): Descriptor? {
        scan()
        return cache[key] ?: return null
    }


    private val Annotation.refMethod
        get() = when (this) {
            is Get -> HttpMethod.GET
            is Post -> HttpMethod.POST
            else -> error("Unsupported annotation $this")
        }

    private val Annotation.refPath
        get() = when (this) {
            is Get -> path
            is Post -> path
            else -> error("Unsupported annotation $this")
        }.trim('/')

    private val Annotation.refMime
        get() = when (this) {
            is Get -> mime
            is Post -> mime
            else -> error("Unsupported annotation $this")
        }
}
