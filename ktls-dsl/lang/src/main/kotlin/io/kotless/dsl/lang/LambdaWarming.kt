package io.kotless.dsl.lang

/**
 * Logic that should be executed during warming sequence of lambda.
 *
 * Interface should be implemented by a static object
 */
interface LambdaWarming {
    fun warmup()
}
