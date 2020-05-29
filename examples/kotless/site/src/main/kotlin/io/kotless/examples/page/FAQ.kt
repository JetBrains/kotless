package io.kotless.examples.page

import io.kotless.dsl.lang.http.Get
import io.kotless.examples.site.pages.FAQPages

object FAQ {
    @Get("/pages/faq")
    fun faq() = FAQPages.faq()
}
