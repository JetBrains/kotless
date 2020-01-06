package io.kotless.local.ktor

import io.kotless.Constants
import io.kotless.dsl.ktor.Kotless
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlin.reflect.full.primaryConstructor

fun main() {
    val port = System.getenv(Constants.Local.serverPort).toInt()
    val classToStart = System.getenv(Constants.Local.Ktor.classToStart)

    val ktClass = ::main::class.java.classLoader.loadClass(classToStart).kotlin
    val instance = (ktClass.primaryConstructor?.call() ?: ktClass.objectInstance) as? Kotless

    val kotless = instance ?: error("The entry point $classToStart does not inherit from ${Kotless::class.qualifiedName}!")

    embeddedServer(Netty, port) {
        kotless.prepare(this)
    }.start(wait = true)
}
