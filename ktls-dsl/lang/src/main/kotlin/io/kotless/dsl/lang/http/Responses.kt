package io.kotless.dsl.lang.http

import io.kotless.MimeType
import io.kotless.dsl.events.HttpResponse

/** Redirect response (code 302) */
fun redirect(href: String) = HttpResponse(302, hashMapOf("Location" to href), null)

/** Bad request error response (code 400) */
fun badRequest(message: String? = null, mime: MimeType = MimeType.PLAIN) = HttpResponse(400, mime, message)

/** Unauthorized (forbidden) response (code 403) */
fun unauthorized(message: String? = null, mime: MimeType = MimeType.PLAIN) = HttpResponse(403, mime, message)

/** Not found response (code 404) */
fun notFound(message: String? = null, mime: MimeType = MimeType.PLAIN) = HttpResponse(404, mime, message)

/** Internal server error response (code 500) */
fun serverError(message: String? = null, mime: MimeType = MimeType.PLAIN) = HttpResponse(500, mime, message)

/** HTML string response (code 200) */
fun html(value: String) = okResponse(value, MimeType.HTML)

/** JSON string response (code 200) */
fun json(value: String) = okResponse(value, MimeType.JSON)

internal fun okResponse(value: String?, mime: MimeType) = HttpResponse(200, mime, value)

