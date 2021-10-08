package io.kotless.examples.page

import io.kotless.dsl.lang.http.Get
import io.kotless.examples.site.pages.MainPages


object Main {
    @Get("/")
    fun root() = "/|" + MainPages.root()
}

