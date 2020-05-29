package io.kotless.examples.bootstrap

import io.ktor.http.content.file
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.http.content.staticRootFolder
import io.ktor.routing.Routing
import java.io.File


fun Routing.siteStatics() {
    //There are used almost all possible definitions of static resources

    static {
        staticRootFolder = File("static")

        static("css") {
            files("css")
        }

        static("js") {
            file("highlight.pack.js", "js/highlight.pack.js")
        }

        file("favicon.apng")
    }
}
