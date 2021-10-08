package io.kotless.examples.page

import io.kotless.dsl.lang.http.Get
import io.kotless.examples.site.pages.IntroductionPages

object Introduction {

    @Get("/pages/introduction")
    fun introduction() = "/pages/introduction|" + IntroductionPages.introduction()
}
