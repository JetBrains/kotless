package io.kotless.parser

import io.kotless.parser.processor.action.GlobalActionsProcessor
import io.kotless.parser.processor.config.EntrypointProcessor
import io.kotless.parser.processor.events.S3EventsProcessor
import io.kotless.parser.processor.events.SQSEventsProcessor
import io.kotless.parser.processor.events.CloudwatchEventsProcessor
import io.kotless.parser.processor.route.DynamicRoutesProcessor
import io.kotless.parser.processor.route.StaticRoutesProcessor

/**
 * KotlessParser parses Kotlin code with Kotlin embeddable compiler looking
 * for Kotless DSL constructs.
 *
 * The result of parsing is a number of Lambdas and StaticResources and associated
 * with them Dynamic and Static routes
 */
object KotlessParser : Parser(
    setOf(
        EntrypointProcessor, GlobalActionsProcessor, DynamicRoutesProcessor, StaticRoutesProcessor, CloudwatchEventsProcessor,
        S3EventsProcessor, SQSEventsProcessor
    )
)
