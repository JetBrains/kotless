package io.kotless.dsl.app.events

import io.kotless.ScheduledEventType
import io.kotless.dsl.Application
import io.kotless.dsl.model.CloudWatch
import io.kotless.dsl.reflection.FunctionCaller
import org.slf4j.LoggerFactory

internal object EventsDispatcher {
    private val logger = LoggerFactory.getLogger(EventsDispatcher::class.java)

    fun process(event: CloudWatch) {
        for (resource in event.resources.map { it.substringAfter("/") }) {
            when {
                resource.contains(ScheduledEventType.Autowarm.prefix) -> {
                    logger.info("Executing warmup sequence")
                    Application.warmup()
                    logger.info("Warmup sequence executed")
                }
                resource.contains(ScheduledEventType.General.prefix) -> {
                    val key = resource.substring(resource.lastIndexOf(ScheduledEventType.General.prefix))

                    logger.info("Executing scheduled lambda with key $key")
                    EventsStorage[key]?.let { FunctionCaller.call(it, emptyMap()) }
                    logger.info("Scheduled lambda with key $key was executed")
                }
            }
        }
    }
}
