package io.kotless.examples.page

import io.kotless.examples.site.pages.MainPages
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
object Main {
    @GetMapping("/")
    fun root() = MainPages.root()
}

