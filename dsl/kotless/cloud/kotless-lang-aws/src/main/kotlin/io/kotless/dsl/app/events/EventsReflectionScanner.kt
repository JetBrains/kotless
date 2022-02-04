package io.kotless.dsl.app.events

import io.kotless.InternalAPI
import io.kotless.ScheduledEventType
import io.kotless.dsl.app.events.processors.*
import io.kotless.dsl.lang.event.*
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

    val processors = listOf(ScheduledAnnotationProcessor, S3AnnotationProcessor, SQSAnnotationProcessor)

    private val logger = LoggerFactory.getLogger(EventsReflectionScanner::class.java)
    fun getEvents(): Set<Data> {
        logger.info("Events processing")
        return processors.flatMap { it.process() }.toSet()
    }

}
