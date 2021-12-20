package io.kotless.dsl.app.events

import io.kotless.InternalAPI
import io.kotless.ScheduledEventType
import io.kotless.dsl.lang.event.S3Event
import io.kotless.dsl.lang.event.Scheduled
import io.kotless.dsl.reflection.ReflectionScanner
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import kotlin.math.absoluteValue
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.kotlinFunction

@InternalAPI
object EventsReflectionScanner {
    data class Data(val ids: Set<EventKey>, val method: Method, val annotation: Annotation)

    private val logger = LoggerFactory.getLogger(EventsReflectionScanner::class.java)
    fun getEvents(): Set<Data> {
        val events = HashSet<Data>()
        logger.info("Events processing")
        for (method in ReflectionScanner.methodsWithAnnotation<Scheduled>()) {
            val annotation = method.toScheduledAnnotation() ?: continue
            events.add(Data(method.scheduledIDs(), method, annotation))
        }
        for (method in ReflectionScanner.methodsWithAnnotation<S3Event>()) {
            val annotation = method.toS3Annotation() ?: continue
            events.add(Data(method.s3IDs(), method, annotation))
        }

        return events
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

    private fun Method.toS3Annotation() = (kotlinFunction as KFunction<*>).findAnnotation<S3Event>()
    private fun Method.s3IDs(): Set<EventKey> {
        val annotation = toS3Annotation()!!
        return "${annotation.bucket}:${annotation.type}".takeIf { it.isNotBlank() }?.let { setOf(EventKey(it)) } ?: emptySet()
    }
}
