package io.kotless.examples

import io.kotless.dsl.ktor.Kotless
import io.kotless.dsl.ktor.lang.LambdaWarming
import io.kotless.dsl.ktor.lang.event.events
import io.kotless.examples.bootstrap.siteStatics
import io.kotless.examples.page.DSL
import io.kotless.examples.page.DSL.events
import io.kotless.examples.page.DSL.http
import io.kotless.examples.page.DSL.lifecycle
import io.kotless.examples.page.DSL.overview
import io.kotless.examples.page.DSL.permissions
import io.kotless.examples.page.FAQ
import io.kotless.examples.page.FAQ.faq
import io.kotless.examples.page.Introduction
import io.kotless.examples.page.Introduction.introduction
import io.kotless.examples.page.Main.main
import io.kotless.examples.page.Plugin
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.*

class Server : Kotless() {
    override fun prepare(app: Application) {
        app.routing {
            siteStatics()

            get("/") {
                call.respondHtml { main() }
            }

            //Supports route inner calls
            route("pages") {
                get("/introduction") {
                    call.respondHtml { with(Introduction) { introduction() } }
                }

                get("/faq") {
                    call.respondHtml { with(FAQ) { faq() } }
                }


                route("/dsl") {
                    get("/overview") {
                        call.respondHtml { with(DSL) { overview() } }
                    }
                    get("/lifecycle") {
                        call.respondHtml { with(DSL) { lifecycle() } }
                    }
                    get("/permissions") {
                        call.respondHtml { with(DSL) { permissions() } }
                    }
                    get("/http") {
                        call.respondHtml { with(DSL) { http() } }
                    }
                    get("/events") {
                        call.respondHtml { with(DSL) { events() } }
                    }
                }

                route("/plugin") {
                    get("/overview") {
                        call.respondHtml { with(Plugin) { overview() } }
                    }
                    get("/configuration") {
                        call.respondHtml { with(Plugin) { configuration() } }
                    }
                    get("/tasks") {
                        call.respondHtml { with(Plugin) { tasks() } }
                    }
                    get("/extensions") {
                        call.respondHtml { with(Plugin) { extensions() } }
                    }
                }
            }

        }

        app.events {
            subscribe(LambdaWarming) {
                println("Lambda warming execution")
            }
        }
    }
}
