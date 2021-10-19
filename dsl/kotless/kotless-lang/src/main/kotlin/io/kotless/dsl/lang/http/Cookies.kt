package io.kotless.dsl.lang.http

import io.kotless.dsl.model.HttpRequest
import io.kotless.dsl.model.HttpResponse
import java.net.HttpCookie
import java.util.regex.Pattern
import kotlin.collections.set

/** Cookie values parsed from headers */
val HttpRequest.cookie: Map<String, String>
    get() = headers.get("Cookie")?.let { parseCookie(it) }.orEmpty().map { it.name to it.value }.toMap()

/** Add a cookie to HttpResponse */
fun HttpResponse.withCookie(name: String, value: String): HttpResponse {
    val cookie = parseCookie(headers["Set-Cookie"] ?: "")
    cookie += HttpCookie(name, value)
    headers["Set-Cookie"] = cookie.joinToString(separator = ";")
    return this
}

private val cookiePattern = Pattern.compile("([^=]+)=([^;]*);?\\s?")
private fun parseCookie(cookies: String): HashSet<HttpCookie> {
    val set = HashSet<HttpCookie>()
    val matcher = cookiePattern.matcher(cookies)
    while (matcher.find()) {
        set.add(HttpCookie(matcher.group(1), matcher.group(2).trim('"', '\'')))
    }
    return set
}
