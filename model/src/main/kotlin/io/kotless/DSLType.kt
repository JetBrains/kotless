package io.kotless

/**
 * Type of DSL used by Kotless project
 *
 * [lib] is a name of library providing support for this language
 */
enum class DSLType(val lib: String) {
    Kotless("lang"),
    Ktor("ktor-lang"),
    SpringBoot("spring-boot-lang")
}
