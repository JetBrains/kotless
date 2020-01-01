package io.kotless.local

import io.kotless.local.handler.DynamicHandler
import io.kotless.local.handler.StaticHandler


fun main() {
    val port = System.getenv("SERVER_PORT").toInt()

    val local = LocalServer(port, StaticHandler(), DynamicHandler())

    local.start()
    local.join()
}
