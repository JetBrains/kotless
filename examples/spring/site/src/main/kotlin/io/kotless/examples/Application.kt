package io.kotless.examples

import io.kotless.dsl.spring.Kotless
import org.springframework.boot.autoconfigure.SpringBootApplication
import kotlin.reflect.KClass

@SpringBootApplication
open class Application: Kotless() {
    override val bootKlass: KClass<*> = this::class
}
