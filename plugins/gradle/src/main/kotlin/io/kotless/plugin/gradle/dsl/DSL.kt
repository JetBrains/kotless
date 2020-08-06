package io.kotless.plugin.gradle.dsl

import io.kotless.DSLType
import io.kotless.parser.DSLDescriptor
import io.kotless.parser.KotlessDescriptor
import io.kotless.parser.ktor.KtorDescriptor
import io.kotless.parser.spring.SpringBootDescriptor
import io.kotless.plugin.gradle.utils.gradle.myExt
import org.gradle.api.Project

internal var Project.kotless: KotlessDSL
    get() = this.myExt("kotless")
    set(value) {
        this.myExt["kotless"] = value
    }

/** Configuration of Kotless application */
@KotlessDSLTag
fun Project.kotless(configure: KotlessDSL.() -> Unit) {
    kotless = KotlessDSL(this).apply(configure)
}

val DSLType.descriptor: DSLDescriptor
    get() = when (this) {
        DSLType.Kotless -> KotlessDescriptor
        DSLType.Ktor -> KtorDescriptor
        DSLType.SpringBoot -> SpringBootDescriptor
    }
