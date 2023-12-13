package io.kotless.local.spring

import io.kotless.Constants
import io.kotless.InternalAPI
import io.kotless.dsl.spring.Kotless
import org.springframework.boot.SpringApplication
import kotlin.reflect.full.primaryConstructor

@OptIn(InternalAPI::class)
fun main() {
    val port = System.getenv(Constants.Local.serverPort).toInt()
    val classToStart = System.getenv(Constants.Local.KtorOrSpring.classToStart)

    val ktClass = ::main::class.java.classLoader.loadClass(classToStart).kotlin
    val instance = (ktClass.primaryConstructor?.call() ?: ktClass.objectInstance) as? Kotless


    val kotless = instance ?: error("The entry point $classToStart does not inherit from ${Kotless::class.qualifiedName}!")

    val app = SpringApplication(kotless.bootKlass.java)
    app.setDefaultProperties(mapOf("server.port" to port.toString()))
    app.run()
}
