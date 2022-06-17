package io.kotless.dsl.app.events.processors

import io.kotless.InternalAPI
import io.kotless.CloudwatchEventType
import io.kotless.dsl.app.events.AwsEventKey
import io.kotless.dsl.app.events.EventsReflectionScanner
import io.kotless.dsl.lang.event.Cloudwatch
import io.kotless.dsl.reflection.ReflectionScanner
import java.lang.reflect.Method
import kotlin.math.absoluteValue
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.kotlinFunction

@InternalAPI
object CloudwatchAnnotationProcessor : AnnotationProcessor {
    override fun process(): Set<EventsReflectionScanner.Data> {
        return ReflectionScanner.methodsWithAnnotation<Cloudwatch>().mapNotNull { method ->
            val annotation = method.toScheduledAnnotation() ?: return@mapNotNull null
            EventsReflectionScanner.Data(method.scheduledIDs(), method.kotlinFunction!!, annotation)
        }.toSet()
    }

    private fun Method.toScheduledAnnotation() = (kotlinFunction as KFunction<*>).findAnnotation<Cloudwatch>()

    private fun Method.scheduledIDs(): Set<AwsEventKey> {
        val annotation = toScheduledAnnotation()!!
        return annotation.id.takeIf { it.isNotBlank() }?.let { setOf(AwsEventKey(it)) } ?: run {
            val klass = declaringClass.kotlin.qualifiedName!!
            setOf("$klass.$name", "${klass.substringBeforeLast(".")}.$name")
                .map { AwsEventKey("${CloudwatchEventType.General.prefix}-${it.hashCode().absoluteValue}") }
                .toSet()
        }
    }
}
