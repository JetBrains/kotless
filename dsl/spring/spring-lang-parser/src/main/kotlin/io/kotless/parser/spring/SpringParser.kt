package io.kotless.parser.spring

import io.kotless.parser.Parser
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.config.EntrypointProcessor
import io.kotless.parser.spring.processor.route.DynamicRoutesProcessor
import io.kotless.parser.spring.processor.route.StaticRoutesProcessor
import java.io.File

/**
 * SpringParser parses Kotlin code with Kotlin embeddable compiler looking
 * for Kotless DSL constructs.
 *
 * The result of parsing is a number of Lambdas and StaticResources and associated
 * with them Dynamic and Static routes
 */
object SpringParser : Parser(setOf(EntrypointProcessor, DynamicRoutesProcessor)) {
    override fun processResources(resources: Set<File>, context: ProcessorContext) {
        StaticRoutesProcessor.process(resources, context)
    }
}
