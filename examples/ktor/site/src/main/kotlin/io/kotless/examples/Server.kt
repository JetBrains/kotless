package io.kotless.examples

import io.kotless.dsl.ktor.Kotless
import io.kotless.dsl.ktor.lang.LambdaWarming
import io.kotless.dsl.ktor.lang.event.events
import io.kotless.examples.bootstrap.siteStatics
import io.kotless.examples.site.pages.*
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing

class Server : Kotless() {
    override fun prepare(app: Application) {
        app.routing {
            siteStatics()

            get("/") {
                call.respondText(MainPages.root(), ContentType.Text.Html)
            }

            //Supports route inner calls
            route("pages") {
                get("/introduction") {
                    call.respondText(IntroductionPages.introduction(), ContentType.Text.Html)
                }

                get("/faq") {
                    call.respondText(FAQPages.faq(), ContentType.Text.Html)
                }


                route("/dsl") {
                    get("/overview") {
                        call.respondText(DSLPages.overview(), ContentType.Text.Html)
                    }
                    get("/lifecycle") {
                        call.respondText(DSLPages.lifecycle(), ContentType.Text.Html)
                    }
                    get("/permissions") {
                        call.respondText(DSLPages.permissions(), ContentType.Text.Html)
                    }
                    get("/http") {
                        call.respondText(DSLPages.http(), ContentType.Text.Html)
                    }
                    get("/events") {
                        call.respondText(DSLPages.events(), ContentType.Text.Html)
                    }
                }

                route("/plugin") {
                    get("/overview") {
                        call.respondText(PluginPages.overview(), ContentType.Text.Html)
                    }
                    get("/configuration") {
                        call.respondText(PluginPages.configuration(), ContentType.Text.Html)
                    }
                    get("/tasks") {
                        call.respondText(PluginPages.tasks(), ContentType.Text.Html)
                    }
                    get("/extensions") {
                        call.respondText(PluginPages.extensions(), ContentType.Text.Html)
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
