package io.kotless.dsl.events

import io.kotless.ScheduledEventType
import io.kotless.dsl.Application
import io.kotless.dsl.model.CloudWatch
import io.kotless.dsl.reflection.FunctionCaller
import org.slf4j.LoggerFactory

internal object EventsDispatcher {
    private val logger = LoggerFactory.getLogger(EventsDispatcher::class.java)

    fun process(event: CloudWatch) {
        for (resource in event.resources.distinct().map { it.substringAfter("/") }) {
            when {
                resource.startsWith(ScheduledEventType.Autowarm.prefix) -> Application.startWarmingSequence()
                resource.startsWith(ScheduledEventType.General.prefix) -> {
                    logger.info("Got key $resource")

                    EventsCache[resource]?.let {
                        logger.info("Calling Event handler")
                        FunctionCaller.call(it, emptyMap())
                    }
                }
            }
        }
    }
}
