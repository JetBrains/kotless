package io.kotless.examples.page

import io.kotless.examples.site.pages.FAQPages
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pages")
object FAQ {

    @RequestMapping("/faq")
    fun faq() = FAQPages.faq()
}
