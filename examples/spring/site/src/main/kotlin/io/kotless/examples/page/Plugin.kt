package io.kotless.examples.page

import io.kotless.examples.site.pages.PluginPages
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pages/plugin")
object Plugin {
    @GetMapping("/overview")
    fun overview() = PluginPages.overview()

    @GetMapping("/configuration")
    fun configuration() = PluginPages.configuration()

    @GetMapping("/tasks")
    fun tasks() = PluginPages.tasks()

    @GetMapping("/extensions")
    fun extensions() = PluginPages.extensions()
}

