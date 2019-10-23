package io.kotless.parser.ktor.utils

import io.kotless.MimeType
import io.ktor.http.ContentType

fun ContentType.toMime() = MimeType.values().find { it.mimeText == "${this.contentType}/${this.contentSubtype}" }
