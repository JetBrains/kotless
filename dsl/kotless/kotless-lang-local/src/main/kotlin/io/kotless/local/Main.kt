package io.kotless.local

import io.kotless.Constants
import io.kotless.InternalAPI

@OptIn(InternalAPI::class)
fun main() {
    val port = System.getenv(Constants.Local.serverPort).toInt()
    val local = LocalServer(port)

    local.start()
    local.join()
}
