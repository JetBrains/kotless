package io.kotless.examples.site.bootstrap

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

                dropdown("DSL", listOf(
                    "Overview" to "/pages/dsl/overview",
                    "Lifecycle" to "/pages/dsl/lifecycle",
                    "HTTP" to "/pages/dsl/http",
                    "Events" to "/pages/dsl/events",
                    "Permissions" to "/pages/dsl/permissions"
                ))

                dropdown("Plugin", listOf(
                    "Overview" to "/pages/plugin/overview",
                    "Configuration" to "/pages/plugin/configuration",
                    "Tasks" to "/pages/plugin/tasks",
                    "Extensions" to "/pages/plugin/extensions"
                ))

                li("nav-item") {
                    a(href = "/pages/faq", classes = "nav-link") {
                        +"FAQ"
                    }
                }
            }
        }
    }
}
