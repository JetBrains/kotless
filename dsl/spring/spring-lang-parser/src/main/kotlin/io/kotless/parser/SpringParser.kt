package io.kotless.parser

import io.kotless.parser.processor.config.EntrypointProcessor
import io.kotless.parser.processor.route.DynamicRoutesProcessor

/**
 * SpringParser parses Kotlin code with Kotlin embeddable compiler looking
 * for Kotless DSL constructs.
 *
 * The result of parsing is a number of Lambdas and StaticResources and associated
 * with them Dynamic and Static routes
 */
object SpringParser : Parser(setOf(EntrypointProcessor, DynamicRoutesProcessor))
