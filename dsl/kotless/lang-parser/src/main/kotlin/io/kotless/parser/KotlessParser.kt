package io.kotless.parser

import io.kotless.parser.processor.action.GlobalActionsProcessor
import io.kotless.parser.processor.events.ScheduledEventsProcessor
import io.kotless.parser.processor.route.DynamicRoutesProcessor
import io.kotless.parser.processor.route.StaticRoutesProcessor

/**
 * KotlessParser parses Kotlin code with Kotlin embeddable compiler looking
 * for Kotless DSL constructs.
 *
 * The result of parsing is a number of Lambdas and StaticResources and associated
 * with them Dynamic and Static routes
 */
object KotlessParser: Parser(setOf(GlobalActionsProcessor, DynamicRoutesProcessor, StaticRoutesProcessor, ScheduledEventsProcessor)) {}
