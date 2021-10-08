package io.kotless.examples.page

import io.kotless.dsl.lang.http.Get
import io.kotless.examples.site.pages.DSLPages

object DSL {
    @Get("/pages/dsl/overview")
    fun overview() = "/pages/dsl/overview|" + DSLPages.overview()

    @Get("/pages/dsl/lifecycle")
    fun lifecycle() = "/pages/dsl/lifecycle|" +DSLPages.lifecycle()

    @Get("/pages/dsl/permissions")
    fun permissions() = "/pages/dsl/permissions|" +DSLPages.permissions()

    @Get("/pages/dsl/http")
    fun http() = "/pages/dsl/http|" +DSLPages.http()

    @Get("/pages/dsl/events")
    fun events() = "/pages/dsl/events|" +DSLPages.events()
}


