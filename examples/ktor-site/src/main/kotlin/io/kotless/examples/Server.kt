package io.kotless.examples

import io.kotless.dsl.ktor.Kotless
import io.kotless.dsl.ktor.lang.LambdaWarming
import io.kotless.dsl.ktor.lang.event.events
import io.kotless.examples.bootstrap.siteStatics
import io.kotless.examples.page.FAQ.faq
import io.kotless.examples.page.Introduction.introduction
import io.kotless.examples.page.Main.main
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
                    call.respondHtml { introduction() }
                }

                get("/faq") {
                    call.respondHtml { faq() }
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
