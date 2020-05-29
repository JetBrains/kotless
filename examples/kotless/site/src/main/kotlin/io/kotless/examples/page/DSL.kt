package io.kotless.examples.page

import io.kotless.dsl.lang.http.Get
import io.kotless.examples.site.pages.DSLPages

object DSL {
    @Get("/pages/dsl/overview")
    fun overview() = DSLPages.overview()

    @Get("/pages/dsl/lifecycle")
    fun lifecycle() = DSLPages.lifecycle()

    @Get("/pages/dsl/permissions")
    fun permissions() = DSLPages.permissions()

    @Get("/pages/dsl/http")
    fun http() = DSLPages.http()

    @Get("/pages/dsl/events")
    fun events() = DSLPages.events()
}


