package io.kotless.examples.page

import io.kotless.examples.storage.URLStorage
import org.apache.commons.validator.routines.UrlValidator
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.view.RedirectView

@RestController
object Shortener {
    @GetMapping("/r")
    fun redirectUrl(k: String): RedirectView {
        val url = URLStorage.getByCode(k)
        if (url == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown short URL")
        } else {
            return RedirectView(url)
        }
    }


    private val logger = LoggerFactory.getLogger("ShortenerKt")

    @GetMapping("/shorten")
    fun shorten(value: String): String {
        logger.info("URL for shortening $value")

        val url = if (value.contains("://").not()) "https://$value" else value

        if (UrlValidator.getInstance().isValid(url).not()) {
            return "Non valid URL"
        }

        val code = URLStorage.getByUrl(url) ?: URLStorage.createCode(url)

        return "https://spring.short.kotless.io/r?k=${code}"
    }

}

