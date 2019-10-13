package io.kotless.examples.bootstrap

import io.kotless.dsl.lang.http.href
import io.kotless.examples.page.*
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

fun UL.dropdown(label: String, labelsToHref: List<Pair<String, String>>) {
    li("nav-item dropdown") {
        a(href = "#", classes = "nav-link dropdown-toggle") {
            attributes["id"] = "dropdown-dsl"
            attributes["data-toggle"] = "dropdown"
            attributes["aria-haspopup"] = "true"
            attributes["aria-expanded"] = "false"
            +label
        }
        div("dropdown-menu") {
            attributes["aria-labelledby"] = "dropdown-dsl"
            for ((btnLabel, href) in labelsToHref) {
                a(href = href, classes = "dropdown-item") {
                    +btnLabel
                }
            }
        }
    }
}


fun BODY.siteNavbar() {
    navbar {
        brand(::faviconIco.href, "Kotless", Main::root.href)

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
                    a(href = Introduction::introduction.href, classes = "nav-link") {
                        +"Getting Started"
                    }
                }

                dropdown("DSL", listOf(
                    "Overview" to DSL::overview.href,
                    "Lifecycle" to DSL::lifecycle.href,
                    "HTTP" to DSL::http.href,
                    "Events" to DSL::events.href,
                    "Permissions" to DSL::permissions.href
                ))

                dropdown("Plugin", listOf(
                    "Overview" to Plugin::overview.href,
                    "Configuration" to Plugin::configuration.href,
                    "Tasks" to Plugin::tasks.href,
                    "Extensions" to Plugin::extensions.href
                ))

                li("nav-item") {
                    a(href = FAQ::faq.href, classes = "nav-link") {
                        +"FAQ"
                    }
                }
            }
        }
    }
}
