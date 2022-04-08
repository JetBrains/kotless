package io.kotless.dsl.app.events.processors

import io.kotless.InternalAPI
import io.kotless.dsl.app.events.EventsReflectionScanner
import io.kotless.dsl.lang.event.*
import io.kotless.dsl.model.events.*
import io.kotless.dsl.reflection.ReflectionScanner
import org.slf4j.LoggerFactory

@InternalAPI
object CustomAwsEventGeneratorAnnotationProcessor : AnnotationProcessor {
    val logger = LoggerFactory.getLogger(CustomAwsEventGeneratorAnnotationProcessor::class.java)
    override fun process(): Set<EventsReflectionScanner.Data> {
        logger.info("Process generators")
        ReflectionScanner.classesWithAnnotation<CustomEventGenerator>().map { clazz ->
            val generatorInstance = clazz.kotlin.objectInstance as AwsEventGenerator
            logger.info("Process generator ${generatorInstance.javaClass.canonicalName}")
            AwsEvent.eventKSerializers.add(generatorInstance)
        }.toSet()
        return emptySet()
    }
}
