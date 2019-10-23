package io.kotless.examples.page

import io.kotless.dsl.Kotless
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing

class Main: Kotless() {
    override fun prepare(app: Application) {
        app.routing {
            get("/") {
                call.respondText { "Hello World!" }
            }
            get("/text") {
                call.respondText { "Hello My World!" }
            }
        }
    }
}
