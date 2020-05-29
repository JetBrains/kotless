package io.kotless.buildsrc

import org.gradle.api.DomainObjectCollection

internal inline fun <reified S : Any> DomainObjectCollection<in S>.myWithType(noinline configuration: S.() -> Unit) =
    withType(S::class.java, configuration)
