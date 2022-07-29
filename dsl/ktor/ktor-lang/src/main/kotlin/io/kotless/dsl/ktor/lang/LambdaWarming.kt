package io.kotless.dsl.ktor.lang

import io.ktor.server.application.Application
import io.ktor.events.EventDefinition

/**
 * Event that will be emitted during warming of lambda.
 */
val LambdaWarming = EventDefinition<Application>()
