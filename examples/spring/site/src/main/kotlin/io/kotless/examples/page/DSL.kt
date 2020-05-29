package io.kotless.examples.page

import io.kotless.examples.site.pages.DSLPages
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pages/dsl")
object DSL {
    @GetMapping("/overview")
    fun overview() = DSLPages.overview()

    @GetMapping("/lifecycle")
    fun lifecycle() = DSLPages.lifecycle()

    @GetMapping("/permissions")
    fun permissions() = DSLPages.permissions()

    @GetMapping("/http")
    fun http() = DSLPages.http()


    @GetMapping("/events")
    fun events() = DSLPages.events()
}


