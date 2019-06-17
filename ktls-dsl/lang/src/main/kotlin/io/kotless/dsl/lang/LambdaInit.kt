package io.kotless.dsl.lang

/**
 * Logic that should be executed during initializing sequence of lambda.
 *
 * Interface should be implemented by a static object
 */
interface LambdaInit {
    fun init()
}
