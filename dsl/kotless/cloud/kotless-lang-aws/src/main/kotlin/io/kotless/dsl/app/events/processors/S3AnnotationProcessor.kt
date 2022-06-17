package io.kotless.dsl.app.events.processors

import io.kotless.InternalAPI
import io.kotless.dsl.app.events.AwsEventKey
import io.kotless.dsl.app.events.EventsReflectionScanner
import io.kotless.dsl.lang.event.S3Event
import io.kotless.dsl.reflection.ReflectionScanner
import java.lang.reflect.Method
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.kotlinFunction

@InternalAPI
object S3AnnotationProcessor : AnnotationProcessor {
    override fun process(): Set<EventsReflectionScanner.Data> {
        return ReflectionScanner.methodsWithAnnotation<S3Event>().mapNotNull { method ->
            val annotation = method.toS3Annotation() ?: return@mapNotNull null
            EventsReflectionScanner.Data(method.s3IDs(), method.kotlinFunction!!, annotation)
        }.toSet()
    }

    private fun Method.toS3Annotation() = (kotlinFunction as KFunction<*>).findAnnotation<S3Event>()
    private fun Method.s3IDs(): Set<AwsEventKey> {
        val annotation = toS3Annotation()!!
        return "${annotation.bucket}:${annotation.type}".takeIf { it.isNotBlank() }?.let { setOf(AwsEventKey(it)) } ?: emptySet()
    }
}
