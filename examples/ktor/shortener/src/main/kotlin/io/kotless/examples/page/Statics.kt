package io.kotless.examples.page

import io.ktor.http.content.*
import io.ktor.routing.Routing
import java.io.File

fun Routing.siteStatics() {
    static {
        staticRootFolder = File("src/main/resources/static")

        static("css") {
            file("shortener.css", "css/shortener.css")
        }

        static("js") {
            file("shortener.js", "js/shortener.js")
        }

        file("favicon.apng", "favicon.apng")
    }
}
