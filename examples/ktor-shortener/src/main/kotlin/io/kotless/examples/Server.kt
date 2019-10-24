package io.kotless.examples

import io.kotless.dsl.ktor.Kotless
import io.kotless.examples.page.Main.main
import io.kotless.examples.page.siteStatics
import io.kotless.examples.storage.URLStorage
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.response.*
import io.ktor.routing.get
import io.ktor.routing.routing
import org.apache.commons.validator.routines.UrlValidator
import org.slf4j.LoggerFactory

class Server : Kotless() {
    private val logger = LoggerFactory.getLogger(Server::class.java)

    override fun prepare(app: Application) {
        app.routing {
            siteStatics()

            get("/") {
                call.respondHtml { main() }
            }

            get("/r") {
                val k = call.parameters["k"]!!

                val url = URLStorage.getByCode(k)
                if (url == null) {
                    call.respond(HttpStatusCode.NotFound, "Unknown short URL")
                } else {
                    call.respondRedirect(url)
                }
            }

            get("/shorten") {
                val value = call.parameters["value"]!!

                logger.info("URL for shortening $value")

                val url = if (value.contains("://").not()) "https://$value" else value

                if (UrlValidator.getInstance().isValid(url).not()) {
                    call.respondText { "Non valid URL" }
                } else {
                    val code = URLStorage.getByUrl(url) ?: URLStorage.createCode(url)
                    call.respondText { "https://short.kotless.io/r?k=$code" }
                }
            }
        }
    }
}
