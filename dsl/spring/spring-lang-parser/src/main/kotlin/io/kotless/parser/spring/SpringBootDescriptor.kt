package io.kotless.parser.spring

import io.kotless.parser.DSLDescriptor

object SpringBootDescriptor: DSLDescriptor {
    override val name: String = "spring-boot"
    override val localEntryPoint: String = "io.kotless.local.spring.MainKt"
}
