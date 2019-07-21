package io.kotless


/** Http methods supported by Kotless Web application */
enum class HttpMethod {
    GET,
    POST
}

/** Mime types supported by StaticResource */
enum class MimeType(val mimeText: String, val isBinary: Boolean) {
    PLAIN("text/plain", false),
    HTML("text/html", false),
    CSS("text/css", false),
    JS("text/javascript", false),

    PNG("image/png", true),
    APNG("image/apng", true),
    GIF("image/gif", true),
    JPEG("image/jpeg", true),
    BMP("image/bmp", true),
    WEBP("image/webp", true),

    JSON("application/json", false),
    XML("application/xml", false),
    ZIP("application/zip", true),
    GZIP("application/gzip", true);

    companion object {
        fun binary() = values().filter { it.isBinary }.toTypedArray()
    }
}
