package io.kotless.dsl.lang.event


@Target(AnnotationTarget.FUNCTION)
annotation class S3Event(val bucket: String, val type: String)
