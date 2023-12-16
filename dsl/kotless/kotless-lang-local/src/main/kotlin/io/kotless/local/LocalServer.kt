package io.kotless.local

import io.kotless.InternalAPI
import io.kotless.dsl.HandlerAWS
import io.kotless.local.handler.DynamicHandler
import io.kotless.local.handler.StaticHandler
import io.kotless.local.scheduled.Scheduler
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.HandlerList

@OptIn(InternalAPI::class)
internal class LocalServer(port: Int) {
    private val handler = HandlerAWS()

    private val server: Server = Server(port)
    private val scheduler: Scheduler = Scheduler(handler)

    init {
        server.handler = HandlerList(StaticHandler(), DynamicHandler(handler))
        server.stopAtShutdown = true
    }

    fun start() {
        server.start()
        scheduler.start()
    }

    fun join() {
        server.join()
    }

    fun stop() {
        scheduler.stop()
        server.stop()
    }
}
