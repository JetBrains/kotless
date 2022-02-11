package io.kotless.dsl.app.events.processors

import io.kotless.InternalAPI
import io.kotless.ScheduledEventType
import io.kotless.dsl.app.events.EventKey
import io.kotless.dsl.app.events.EventsReflectionScanner
import io.kotless.dsl.lang.event.*
import io.kotless.dsl.reflection.ReflectionScanner
import java.lang.reflect.Method
import kotlin.math.absoluteValue
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.kotlinFunction

@InternalAPI
object SQSAnnotationProcessor : AnnotationProcessor {
    override fun process(): Set<EventsReflectionScanner.Data> {
        return ReflectionScanner.methodsWithAnnotation<SQSEvent>().mapNotNull { method ->
            val annotation = method.toSQSAnnotation() ?: return@mapNotNull null
            EventsReflectionScanner.Data(method.sqsIDs(), method, annotation)
        }.toSet()
    }

    private fun Method.toSQSAnnotation() = (kotlinFunction as KFunction<*>).findAnnotation<SQSEvent>()
    private fun Method.sqsIDs(): Set<EventKey> {
        val annotation = toSQSAnnotation()!!
        return annotation.queueArn.takeIf { it.isNotBlank() }?.let { setOf(EventKey(it)) } ?: emptySet()
    }
}