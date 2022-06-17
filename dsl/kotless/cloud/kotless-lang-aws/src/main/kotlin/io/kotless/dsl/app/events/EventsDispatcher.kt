package io.kotless.dsl.app.events

import io.kotless.InternalAPI
import io.kotless.dsl.model.events.AwsEvent
import io.kotless.dsl.reflection.FunctionCaller
import org.slf4j.LoggerFactory

@InternalAPI
object EventsDispatcher {
    private val logger = LoggerFactory.getLogger(EventsDispatcher::class.java)

    fun process(event: AwsEvent) {
        for (record in event.records()) {
            val key = record.path
            logger.info("Executing aws event with key $key")
            val eventProcessors = EventsStorage.getAll(AwsEventKey(key))
            logger.info("Event processors: ${eventProcessors.joinToString { it.name }}")
            eventProcessors.forEach { eventProcessor ->
                val parameters = eventProcessor.parameters.firstOrNull()?.name?.let { mapOf(it to record) }
                FunctionCaller.callWithParams(eventProcessor, parameters ?: emptyMap())
                logger.info("Aws event with key $key was handled")
            }
        }
    }
}
