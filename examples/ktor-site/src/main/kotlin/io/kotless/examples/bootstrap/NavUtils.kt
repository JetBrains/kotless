package io.kotless.examples.bootstrap

import kotlinx.html.*


fun BODY.navbar(body: NAV.() -> Unit) {
    nav("navbar navbar-expand-sm navbar-dark bg-dark fixed-top") {
        body()
    }
}

fun NAV.brand(labelImg: String, text: String, iconHref: String) {
    img(alt = "Kotless label", src = labelImg, classes = "img-label")
    a(iconHref, classes = "navbar-brand") {
        +text
    }
}

fun BODY.siteNavbar() {
    navbar {
        brand("/favicon.apng", "Kotless", "/")

        button(classes = "navbar-toggler collapsed") {
            type = ButtonType.button
            attributes["data-toggle"] = "collapse"
            attributes["data-target"] = "#navbar-toggler"
            attributes["aria-controls"] = "navbar-toggler"
            attributes["aria-expanded"] = "false"
            attributes["aria-label"] = "Toggle navigation"
            span(classes = "navbar-toggler-icon") { }
        }

        div(classes = "navbar-collapse collapse") {
            style = ""
            id = "navbar-toggler"

            ul("navbar-nav") {
                li("nav-item") {
                    a(href = "/pages/introduction", classes = "nav-link") {
                        +"Getting Started"
                    }
                }

                li("nav-item") {
                    a(href = "/pages/faq", classes = "nav-link") {
                        +"FAQ"
                    }
                }
            }
        }
    }
}
