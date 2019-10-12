package io.kotless.examples.page

import io.kotless.dsl.lang.http.*
import io.kotless.dsl.model.HttpResponse
import io.kotless.examples.storage.URLStorage
import org.apache.commons.validator.routines.UrlValidator
import org.slf4j.LoggerFactory

@Get("/r")
fun redirectUrl(k: String): HttpResponse {
    val url = URLStorage.getByCode(k)
    return if (url == null) {
        notFound("Unknown short URL")
    } else {
        redirect(url)
    }
}


private val logger = LoggerFactory.getLogger("ShortenerKt")

@Get("/shorten")
fun shorten(value: String): String {
    logger.info("URL for shortening $value")

    val url = if (value.contains("://").not()) "https://$value" else value

    if (UrlValidator.getInstance().isValid(url).not()) {
        return "Non valid URL"
    }

    val code = URLStorage.getByUrl(url) ?: URLStorage.createCode(url)

    return "https://short.kotless.io${::redirectUrl.href(code)}"
}
