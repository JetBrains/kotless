package io.kotless

/** API marked with this annotation is Kotless internal and it is not intended to be used outside. */
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.CONSTRUCTOR
)
annotation class InternalAPI
