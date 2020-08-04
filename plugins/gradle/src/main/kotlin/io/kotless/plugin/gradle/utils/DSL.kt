package io.kotless.plugin.gradle.utils

import io.kotless.DSLType
import io.kotless.parser.DSLDescriptor
import io.kotless.parser.KotlessDescriptor
import io.kotless.parser.ktor.KtorDescriptor
import io.kotless.parser.spring.SpringBootDescriptor

val DSLType.descriptor: DSLDescriptor
    get() = when (this) {
        DSLType.Kotless -> KotlessDescriptor
        DSLType.Ktor -> KtorDescriptor
        DSLType.SpringBoot -> SpringBootDescriptor
    }
