package io.kotless.dsl.app.events

import io.kotless.InternalAPI
import io.kotless.ScheduledEventType
import io.kotless.dsl.Application
import io.kotless.dsl.cloud.aws.CloudWatch
import io.kotless.dsl.model.events.AwsEvent
import io.kotless.dsl.reflection.FunctionCaller
import org.slf4j.LoggerFactory

@InternalAPI
object EventsDispatcher {
    private val logger = LoggerFactory.getLogger(EventsDispatcher::class.java)

    fun process(event: CloudWatch) {
        for (resource in event.resources.map { it.substringAfter("/") }) {
            when {
                resource.contains(ScheduledEventType.Autowarm.prefix) -> {
                    logger.trace("Executing warmup sequence")
                    Application.warmup()
                    logger.trace("Warmup sequence executed")
                }
                resource.contains(ScheduledEventType.General.prefix) -> {
                    val key = resource.substring(resource.lastIndexOf(ScheduledEventType.General.prefix))

                    logger.trace("Executing scheduled lambda with key $key")
                    EventsStorage[EventKey(key)]?.let { FunctionCaller.call(it, emptyMap()) }
                    logger.trace("Scheduled lambda with key $key was executed")
                }
            }
        }
    }

    fun process(event: AwsEvent) {
        for (record in event.records()) {
            val key = record.path
            logger.info("Executing aws event with key $key")
            val eventProcessors = EventsStorage.getAll(EventKey(key))
            eventProcessors.forEach { eventProcessor ->
                FunctionCaller.callWithParams(eventProcessor, mapOf(eventProcessor.parameters.first().name!! to record))
                logger.info("Aws event with key $key was handled")
            }
        }
    }
}
