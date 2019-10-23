package io.kotless

import java.io.File


/** Http methods supported by Kotless Web application */
enum class HttpMethod {
    GET,
    POST
}

/** Mime types supported by StaticResource */
enum class MimeType(val mimeText: String, val isBinary: Boolean, val extension: String) {
    PLAIN("text/plain", false, "txt"),
    HTML("text/html", false, "html"),
    CSS("text/css", false, "css"),

    PNG("image/png", true, "png"),
    APNG("image/apng", true, "apng"),
    GIF("image/gif", true, "gif"),
    JPEG("image/jpeg", true, "jpeg"),
    BMP("image/bmp", true, "bmp"),
    WEBP("image/webp", true, "webp"),

    JS("application/javascript", false, "js"),
    JSON("application/json", false, "json"),
    XML("application/xml", false, "xml"),
    ZIP("application/zip", true, "zip"),
    GZIP("application/gzip", true, "gzip");

    companion object {
        fun binary() = values().filter { it.isBinary }.toTypedArray()
        fun forFile(file: File) = values().find { it.extension == file.extension }
    }
}
