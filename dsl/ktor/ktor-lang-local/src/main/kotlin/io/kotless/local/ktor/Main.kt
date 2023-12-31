package io.kotless.local.ktor

import io.kotless.Constants
import io.kotless.InternalAPI
import io.kotless.dsl.ktor.KotlessAWS
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlin.reflect.full.primaryConstructor

@OptIn(InternalAPI::class)
fun main() {
    val port = System.getenv(Constants.Local.serverPort).toInt()
    val classToStart = System.getenv(Constants.Local.KtorOrSpring.classToStart)

    val ktClass = ::main::class.java.classLoader.loadClass(classToStart).kotlin
    val instance = (ktClass.primaryConstructor?.call() ?: ktClass.objectInstance) as? KotlessAWS

    val kotless = instance ?: error("The entry point $classToStart does not inherit from ${KotlessAWS::class.qualifiedName}!")

    val embeddedServer = embeddedServer(Netty, port) {
        kotless.prepare(this)
    }

    embeddedServer.start(wait = true)

    while (true) {
        print("type 'exit' to close the app gracefully: ")
        val line = readlnOrNull()

        if(line == "exit") {
            break
        }
    }

    embeddedServer.stop(0, 0)
}
