package io.kotless.dsl.ktor.lang.event

import io.ktor.application.*

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
fun Application.events(body: ApplicationEvents.() -> Unit) {
    this.environment.monitor.body()
}
