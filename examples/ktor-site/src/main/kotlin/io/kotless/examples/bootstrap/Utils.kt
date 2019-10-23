package io.kotless.examples.bootstrap

import kotlinx.html.*


fun FlowContent.kotlin(value: String) {
    pre {
        code(classes = "kotlin") {
            +value.trimIndent()
        }
    }
}

fun HTML.doc(body: DIV.() -> Unit) {
    headerSite()
    body {
        siteNavbar()

        mainDoc {
            body()
        }
    }
}

fun HTML.landing(body: DIV.() -> Unit) {
    headerSite()
    body {
        siteNavbar()

        mainLanding {
            body()
        }
    }
}
