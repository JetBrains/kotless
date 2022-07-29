package io.kotless.dsl.ktor.lang.event

import io.ktor.server.application.Application
import io.ktor.events.Events

/**
 * DSL Marker for DSL to define events in Ktor
 */
@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class EventsDsl

/**
 * Configuration method to define applications events
 */
@EventsDsl
fun Application.events(body: Events.() -> Unit) {
    this.environment.monitor.body()
}
