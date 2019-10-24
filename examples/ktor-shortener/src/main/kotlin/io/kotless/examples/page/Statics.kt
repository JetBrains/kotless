package io.kotless.examples.page

import io.ktor.http.content.file
import io.ktor.http.content.static
import io.ktor.routing.Routing

fun Routing.siteStatics() {
    static("css") {
        file("shortener.css", "css/shortener.css")
    }
    static("js") {
        file("shortener.js", "js/shortener.js")
    }
    static {
        file("favicon.apng", "favicon.apng")
    }
}
