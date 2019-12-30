package io.kotless.dsl.ktor

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlin.reflect.full.primaryConstructor

fun main() {
    val port = System.getenv("KTOR_PORT").toInt()
    val classToStart = System.getenv("CLASS_TO_START")

    val ktClass = ::main.javaClass.classLoader.loadClass(classToStart).kotlin
    val instance = ktClass.primaryConstructor?.call() ?: ktClass.objectInstance

    val kotless = instance as? Kotless
        ?: error("The entry point $classToStart does not inherit from ${Kotless::class.qualifiedName}!")

    embeddedServer(Netty, port) {
        kotless.prepare(this)
    }.start(wait = true)
}
