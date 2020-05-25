package io.kotless.examples

import io.kotless.examples.page.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MainController {
    @GetMapping("/")
    fun main(): String = Main.root()

    @GetMapping("/pages/introduction")
    fun introduction(): String = Introduction.introduction()

    @GetMapping("/pages/faq")
    fun faq(): String = FAQ.faq()

    @GetMapping("/pages/plugin/overview")
    fun pluginOverview(): String = Plugin.overview()
    @GetMapping("/pages/plugin/configuration")
    fun pluginConfiguration(): String = Plugin.configuration()
    @GetMapping("/pages/plugin/tasks")
    fun pluginTasks(): String = Plugin.tasks()
    @GetMapping("/pages/plugin/extensions")
    fun pluginExtensions(): String = Plugin.extensions()


    @GetMapping("/pages/dsl/overview")
    fun dslOverview(): String = DSL.overview()
    @GetMapping("/pages/dsl/http")
    fun dslHTTP(): String = DSL.http()
    @GetMapping("/pages/dsl/events")
    fun dslEvents(): String = DSL.events()
    @GetMapping("/pages/dsl/lifecycle")
    fun dslLifecycle(): String = DSL.lifecycle()
    @GetMapping("/pages/dsl/permissions")
    fun dslPermissions(): String = DSL.permissions()
}
