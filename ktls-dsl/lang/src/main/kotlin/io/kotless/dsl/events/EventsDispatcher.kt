package io.kotless.dsl.events

import io.kotless.dsl.Application
import io.kotless.dsl.model.CloudWatch
import io.kotless.dsl.reflection.FunctionCaller
import org.slf4j.LoggerFactory

internal object EventsDispatcher {
    private val logger = LoggerFactory.getLogger(EventsDispatcher::class.java)

    fun process(event: CloudWatch) {
        for (resource in event.resources.distinct()) {
            when {
                resource.contains("autowarm") -> Application.startWarmingSequence()
                resource.contains("scheduled") -> {
                    val key = resource.dropWhile { it != '/' }.drop(1)
                    logger.info("Got key $key")

                    EventsCache[key]?.let {
                        logger.info("Calling Event handler")
                        FunctionCaller.call(it, emptyMap())
                    }
                }
            }
        }
    }
}
