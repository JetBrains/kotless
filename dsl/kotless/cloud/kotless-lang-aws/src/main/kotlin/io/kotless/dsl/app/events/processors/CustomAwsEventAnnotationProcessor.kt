package io.kotless.dsl.app.events.processors

import io.kotless.InternalAPI
import io.kotless.dsl.app.events.EventKey
import io.kotless.dsl.app.events.EventsReflectionScanner
import io.kotless.dsl.lang.event.CustomAwsEvent
import io.kotless.dsl.lang.event.S3Event
import io.kotless.dsl.reflection.ReflectionScanner
import java.lang.reflect.Method
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.kotlinFunction

@InternalAPI
object CustomAwsEventAnnotationProcessor : AnnotationProcessor {
    override fun process(): Set<EventsReflectionScanner.Data> {
        return ReflectionScanner.methodsWithAnnotation<CustomAwsEvent>().mapNotNull { method ->
            val annotation = method.toCustomAwsEventAnnotation() ?: return@mapNotNull null
            EventsReflectionScanner.Data(method.s3IDs(), method, annotation)
        }.toSet()
    }

    private fun Method.toCustomAwsEventAnnotation() = (kotlinFunction as KFunction<*>).findAnnotation<CustomAwsEvent>()
    private fun Method.s3IDs(): Set<EventKey> {
        val annotation = toCustomAwsEventAnnotation()!!
        return annotation.path.takeIf { it.isNotBlank() }?.let { setOf(EventKey(it)) } ?: emptySet()
    }
}