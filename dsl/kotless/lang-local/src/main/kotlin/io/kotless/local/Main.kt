package io.kotless.local


fun main() {
    val port = System.getenv("SERVER_PORT").toInt()

    val local = LocalServer(port)

    local.start()
    local.join()
}
