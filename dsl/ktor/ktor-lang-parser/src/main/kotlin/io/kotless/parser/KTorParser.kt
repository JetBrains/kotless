package io.kotless.parser

/**
 * KTorDslParser parses Kotlin code with Kotlin embeddable compiler looking
 * for KTor DSL constructs.
 *
 * The result of parsing is a number of Lambdas and StaticResources and associated
 * with them Dynamic and Static routes
 */
object KTorParser : Parser(emptySet())
