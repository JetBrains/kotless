package io.kotless.gen

import io.kotless.hcl.HCLNamed

data class KotlessGenerationContext(val entities: Map<String, HCLNamed>) {
    inline fun <reified T: HCLNamed> get(name: String) = entities[name] as T
}

interface KotlessFactory<T> {
    fun get(entity: T, context: KotlessGenerationContext): Set<HCLNamed>
}

