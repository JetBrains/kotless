package io.kotless.dsl.lang.event


@Target(AnnotationTarget.FUNCTION)
annotation class SQSEvent(val queueArn: String)
