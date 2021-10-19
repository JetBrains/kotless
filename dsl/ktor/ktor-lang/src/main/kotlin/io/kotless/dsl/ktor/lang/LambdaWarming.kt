package io.kotless.dsl.ktor.lang

import io.ktor.application.*

/**
 * Event that will be emitted during warming of lambda.
 */
val LambdaWarming = EventDefinition<Application>()
