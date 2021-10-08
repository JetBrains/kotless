package io.kotless.examples.page

import io.kotless.dsl.lang.http.Get
import io.kotless.examples.site.pages.PluginPages

object Plugin {
    @Get("/pages/plugin/overview")
    fun overview() = "/pages/plugin/overview|" + PluginPages.overview()

    @Get("/pages/plugin/configuration")
    fun configuration() = "/pages/plugin/configuration|" + PluginPages.configuration()

    @Get("/pages/plugin/tasks")
    fun tasks() = "/pages/plugin/tasks|" + PluginPages.tasks()

    @Get("/pages/plugin/extensions")
    fun extensions() = "/pages/plugin/extensions|" + PluginPages.extensions()
}

