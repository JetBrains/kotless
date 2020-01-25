package io.kotless.parser.spring

import io.kotless.parser.Parser
import io.kotless.parser.processor.config.EntrypointProcessor
import io.kotless.parser.spring.processor.route.DynamicRoutesProcessor

/**
 * SpringParser parses Kotlin code with Kotlin embeddable compiler looking
 * for Kotless DSL constructs.
 *
 * The result of parsing is a number of Lambdas and StaticResources and associated
 * with them Dynamic and Static routes
 */
object SpringParser : Parser(setOf(EntrypointProcessor, DynamicRoutesProcessor))
