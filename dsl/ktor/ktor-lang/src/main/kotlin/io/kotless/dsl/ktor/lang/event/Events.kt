package io.kotless.dsl.ktor.lang.event

import io.ktor.application.Application
import io.ktor.application.ApplicationEvents

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class EventsDsl

@EventsDsl
fun Application.events(body: ApplicationEvents.() -> Unit) {
    this.environment.monitor.body()
}
