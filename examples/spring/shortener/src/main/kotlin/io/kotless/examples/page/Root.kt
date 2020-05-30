package io.kotless.examples.page

import io.kotless.examples.utils.html
import kotlinx.html.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
object Root {
    @GetMapping("/")
    fun root() = html {
        head {
            title {
                +"Shortless"
            }

            link {
                href = "/favicon.ico"
                rel = "icon"
            }

            link {
                href = "https://use.fontawesome.com/releases/v5.8.1/css/all.css"
                rel = "stylesheet"
            }
            link {
                href = "https://fonts.googleapis.com/css?family=Fira+Sans:300,400,600&display=swap"
                rel = "stylesheet"
            }
            link {
                href = "/css/shortener.css"
                rel = "stylesheet"
            }
            script { src = "/js/shortener.js" }
        }
        body {
            div("main-block") {
                input {
                    id = "shorten-input"
                    classes = setOf("url-input")
                    type = InputType.url
                    placeholder = "Shorten your link"
                }
                button {
                    id = "shorten-button"
                    classes = setOf("url-button")
                    type = ButtonType.submit

                    span {
                        id = "shorten-button-text"
                        +"Go!"
                    }
                }
            }
            div("footer-block") {
                span("footer-text") {
                    +"Shortless"
                }
                span("footer-muted-text") {
                    +"Powered by Kotless and Spring Boot"
                }
            }
        }
    }
}

