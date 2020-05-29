package io.kotless.examples.page

import io.kotless.dsl.lang.http.Get
import io.kotless.examples.bootstrap.*
import io.kotless.examples.site.pages.MainPages
import kotlinx.html.*


object Main {
    @Get("/")
    fun root() = MainPages.root()
}

