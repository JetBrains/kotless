package io.kotless.parser

interface DSLDescriptor {
    val name: String

    val parser: Parser

    val apiLibrary: String
        get() = "$name-lang"

    val localLibrary: String
        get() = "$apiLibrary-local"

    val localEntryPoint: String
}
