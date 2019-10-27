package io.kotless.dsl.ktor

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlin.reflect.full.primaryConstructor

fun main() {
    val port = System.getenv("KTOR_PORT").toInt()
    val classToStart = System.getenv("CLASS_TO_START")

    val kotless = ::main.javaClass.classLoader.loadClass(classToStart).kotlin.primaryConstructor!!.call() as Kotless

    embeddedServer(Netty, port) {
        kotless.prepare(this)
    }.start(wait = true)
}
