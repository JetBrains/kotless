package io.kotless.examples.page

import io.kotless.examples.site.pages.IntroductionPages
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pages")
object Introduction {
    @RequestMapping("/introduction", method = [RequestMethod.GET])
    fun introduction() = IntroductionPages.introduction()
}
