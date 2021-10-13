package io.kotless.local

import io.kotless.Constants
import io.kotless.dsl.Application

fun main() {
    val port = System.getenv(Constants.Local.serverPort).toInt()
    val local = LocalServer(port)

    println("RUNNING INIT")

    Application.init()

    local.start()
    local.join()
}
