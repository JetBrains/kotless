package io.kotless.local

import io.kotless.Constants
import io.kotless.InternalAPI

@OptIn(InternalAPI::class)
fun main() {
    val port = System.getenv(Constants.Local.serverPort).toInt()
    val local = LocalServer(port)

    local.start()

    while (true) {
        print("type 'exit' to close the app gracefully: ")
        val line = readlnOrNull()

        if(line == "exit") {
            break
        }
    }
}
