package io.kotless.dsl.app.events

import io.kotless.InternalAPI
import io.kotless.dsl.app.events.processors.*
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import kotlin.reflect.KFunction

@InternalAPI
object EventsReflectionScanner {
    data class Data(val ids: Set<AwsEventKey>, val method: KFunction<*>, val annotation: Annotation? = null)

    val processors = listOf(
        CloudwatchAnnotationProcessor,
        WarmupProcessor,
        S3AnnotationProcessor,
        SQSAnnotationProcessor,
        CustomAwsEventAnnotationProcessor,
        CustomAwsEventGeneratorAnnotationProcessor
    )

    private val logger = LoggerFactory.getLogger(EventsReflectionScanner::class.java)
    fun getEvents(): Set<Data> {
        logger.info("Events processing")
        return processors.flatMap { it.process() }.toSet()
    }

}
