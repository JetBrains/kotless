package io.kotless.dsl.app.events.processors

import io.kotless.InternalAPI
import io.kotless.dsl.Application
import io.kotless.dsl.app.events.AwsEventKey
import io.kotless.dsl.app.events.EventsReflectionScanner
import org.slf4j.LoggerFactory

@InternalAPI
object WarmupProcessor : AnnotationProcessor {
    val logger = LoggerFactory.getLogger(WarmupProcessor::class.java)

    override fun process(): Set<EventsReflectionScanner.Data> {
        val warmupId = AwsEventKey("autowarm")
        return setOf(EventsReflectionScanner.Data(setOf(warmupId), Application::warmup))
    }
}
