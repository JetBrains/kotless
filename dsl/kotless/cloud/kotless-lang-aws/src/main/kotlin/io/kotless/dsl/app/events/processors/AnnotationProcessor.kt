package io.kotless.dsl.app.events.processors

import io.kotless.InternalAPI
import io.kotless.dsl.app.events.EventsReflectionScanner

@InternalAPI
interface AnnotationProcessor {
    fun process(): Set<EventsReflectionScanner.Data>
}
