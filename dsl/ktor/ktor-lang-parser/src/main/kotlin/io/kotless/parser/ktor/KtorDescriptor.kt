package io.kotless.parser.ktor

import io.kotless.parser.DSLDescriptor

object KtorDescriptor: DSLDescriptor {
    override val name: String = "ktor"
    override val localEntryPoint: String = "io.kotless.local.ktor.MainKt"
}
