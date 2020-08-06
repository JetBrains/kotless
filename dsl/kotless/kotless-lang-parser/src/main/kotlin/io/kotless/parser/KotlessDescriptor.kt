package io.kotless.parser

object KotlessDescriptor: DSLDescriptor {
    override val name: String = "kotless"

    override val parser: Parser = KotlessParser

    override val localEntryPoint: String = "io.kotless.local.MainKt"
}
