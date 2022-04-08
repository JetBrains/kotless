package io.kotless.dsl.app.events

import io.kotless.InternalAPI
import io.kotless.dsl.app.events.processors.*
import org.slf4j.LoggerFactory
import java.lang.reflect.Method

@InternalAPI
object EventsReflectionScanner {
    data class Data(val ids: Set<AwsEventKey>, val method: Method, val annotation: Annotation)

    val processors = listOf(
        CloudwatchAnnotationProcessor,
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
