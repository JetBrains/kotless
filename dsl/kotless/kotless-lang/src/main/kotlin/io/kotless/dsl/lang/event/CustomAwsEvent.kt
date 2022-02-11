package io.kotless.dsl.lang.event

@Target(AnnotationTarget.FUNCTION)
annotation class CustomAwsEvent(val path: String)
