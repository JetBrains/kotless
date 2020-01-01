package io.kotless.local

import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.HandlerList

class LocalServer(port: Int, handlers: List<Handler>) {
    private val server: Server = Server(port)

    constructor(port: Int, vararg handlers: Handler) : this(port, handlers.toList())

    init {
        server.handler = HandlerList(*handlers.toTypedArray())
        server.stopAtShutdown = true
    }

    fun start() = server.start()
    fun join() = server.join()
}
