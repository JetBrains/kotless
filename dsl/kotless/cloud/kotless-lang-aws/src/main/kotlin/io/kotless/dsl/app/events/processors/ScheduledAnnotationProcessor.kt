package io.kotless.dsl.app.events.processors

import io.kotless.InternalAPI
import io.kotless.ScheduledEventType
import io.kotless.dsl.app.events.EventKey
import io.kotless.dsl.app.events.EventsReflectionScanner
import io.kotless.dsl.lang.event.Scheduled
import io.kotless.dsl.reflection.ReflectionScanner
import java.lang.reflect.Method
import kotlin.math.absoluteValue
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.kotlinFunction

@InternalAPI
object ScheduledAnnotationProcessor : AnnotationProcessor {
    override fun process(): Set<EventsReflectionScanner.Data> {
        return ReflectionScanner.methodsWithAnnotation<Scheduled>().mapNotNull { method ->
            val annotation = method.toScheduledAnnotation() ?: return@mapNotNull null
            EventsReflectionScanner.Data(method.scheduledIDs(), method, annotation)
        }.toSet()
    }

    private fun Method.toScheduledAnnotation() = (kotlinFunction as KFunction<*>).findAnnotation<Scheduled>()

    private fun Method.scheduledIDs(): Set<EventKey> {
        val annotation = toScheduledAnnotation()!!
        return annotation.id.takeIf { it.isNotBlank() }?.let { setOf(EventKey(it)) } ?: run {
            val klass = declaringClass.kotlin.qualifiedName!!
            setOf("$klass.$name", "${klass.substringBeforeLast(".")}.$name")
                .map { EventKey("${ScheduledEventType.General.prefix}-${it.hashCode().absoluteValue}") }
                .toSet()
        }
    }
}
