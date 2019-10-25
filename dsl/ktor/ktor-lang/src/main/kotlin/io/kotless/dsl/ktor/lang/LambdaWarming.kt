package io.kotless.dsl.ktor.lang

import io.ktor.application.Application
import io.ktor.application.EventDefinition

/**
 * Event that will be emitted during warming of lambda.
 */
val LambdaWarming = EventDefinition<Application>()
