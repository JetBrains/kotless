package io.kotless.dsl.app.events

import io.kotless.InternalAPI
import io.kotless.ScheduledEventType
import io.kotless.dsl.lang.event.Scheduled
import io.kotless.dsl.reflection.ReflectionScanner
import java.lang.reflect.Method
import kotlin.math.absoluteValue
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.kotlinFunction

@InternalAPI
object EventsReflectionScanner {
    data class Data(val ids: Set<String>, val method: Method, val annotation: Scheduled)

    fun getEvents(): Set<Data> {
        val events = HashSet<Data>()

        for (method in ReflectionScanner.methodsWithAnnotation<Scheduled>()) {
            events.add(Data(method.toIDs(), method, method.toAnnotation()!!))
        }

        return events
    }

    private fun Method.toAnnotation() = (kotlinFunction as KFunction<*>).findAnnotation<Scheduled>()

    private fun Method.toIDs(): Set<String> {
        val annotation = toAnnotation()!!

        return annotation.id.takeIf { it.isNotBlank() }?.let { setOf(it) } ?: run {
            val klass = declaringClass.kotlin.qualifiedName!!
            setOf("$klass.$name", "${klass.substringBeforeLast(".")}.$name")
                .map { "${ScheduledEventType.General.prefix}-${it.hashCode().absoluteValue}" }
                .toSet()
        }
    }
}
