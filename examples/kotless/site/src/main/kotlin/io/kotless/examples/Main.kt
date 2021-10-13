package io.kotless.examples

import io.kotless.Constants
import io.kotless.InternalAPI
import io.kotless.dsl.Application
import io.kotless.local.LocalServer

@OptIn(InternalAPI::class)
fun main() {
    val port = 5000
    val local = LocalServer(port)

    println("RUNNING INIT")

    Application.init()

    local.start()
    local.join()
}
