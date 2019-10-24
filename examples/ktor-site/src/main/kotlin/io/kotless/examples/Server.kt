package io.kotless.examples

import com.amazonaws.services.lambda.runtime.*
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import io.kotless.dsl.ktor.Kotless
import io.kotless.examples.bootstrap.siteStatics
import io.kotless.examples.page.FAQ.faq
import io.kotless.examples.page.Introduction.introduction
import io.kotless.examples.page.Main.main
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.get
import io.ktor.routing.routing

class Server : Kotless() {
    override fun prepare(app: Application) {
        app.routing {
            siteStatics()

            get("/") {
                call.respondHtml { main() }
            }

            get("/introduction") {
                call.respondHtml { introduction() }
            }

            get("/faq") {
                call.respondHtml { faq() }
            }
        }
    }
}
