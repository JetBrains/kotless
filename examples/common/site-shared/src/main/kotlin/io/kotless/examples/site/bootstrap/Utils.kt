package io.kotless.examples.site.bootstrap

import kotlinx.html.*
import kotlinx.html.stream.createHTML

fun html(body: TagConsumer<String>.() -> Unit): String {
    return createHTML().apply(body).finalize()
}

fun FlowContent.kotlin(value: String) {
    pre {
        code(classes = "kotlin") {
            +value.trimIndent()
        }
    }
}

fun doc(body: DIV.() -> Unit) = html {
    headerSite()
    body {
        siteNavbar()

        mainDoc {
            body()
        }
    }
}

fun landing(body: DIV.() -> Unit) = html {
    headerSite()
    body {
        siteNavbar()

        mainLanding {
            body()
        }
    }
}
