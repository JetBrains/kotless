package io.kotless.parser.ktor.utils

import io.kotless.MimeType
import io.ktor.http.*

fun ContentType.toMime() = MimeType.values().find { it.mimeText == "${contentType}/${contentSubtype}" }
