package io.kotless.examples.page

import io.kotless.examples.bootstrap.*
import kotlinx.html.*

object Main {
    fun HTML.main() = landing {
        h1 {
            +"Kotlin Serverless Framework"
        }

        br()

        p {
            +"Create and deploy serverless application using Kotlin only."
        }
        br()

        kotlin("""
                //This page code and deployment at once
                @Get("/")
                fun root() = html {
                    header()
                    body {
                        navbar()
                        text()
                        cards()
                    }
                }""".trimIndent())

        row {
            smCol(4) {
                simpleCard("landing-card") {
                    i("fas fa-7x fa-fighter-jet")
                    p("landing-card-header") {
                        +"Fast"
                    }
                    p {
                        +"Warm execution <10ms."
                        br()
                        +"Cold execution <600ms."
                    }
                }
            }

            smCol(4) {
                simpleCard("landing-card") {
                    i("fas fa-7x fa-bezier-curve")
                    p("landing-card-header") {
                        +"Scalable"
                    }
                    p {
                        +"Up to 1000 requests at once."
                        br()
                        +"Automatic scaling."
                    }
                }
            }

            smCol(4) {
                simpleCard("landing-card") {
                    i("fas fa-7x fa-check")
                    p("landing-card-header") {
                        +"Simple"
                    }
                    p {
                        +"Code now is your deployment."
                        br()
                        +"No more pain with cloud."
                    }
                }
            }
        }
    }
}
