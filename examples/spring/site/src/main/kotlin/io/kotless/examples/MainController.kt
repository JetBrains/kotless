package io.kotless.examples

import io.kotless.examples.page.Main
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MainController {
    @GetMapping("/")
    fun main(): String = Main.root()
}
