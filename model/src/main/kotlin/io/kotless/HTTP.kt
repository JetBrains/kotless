package io.kotless

import java.io.File


/** Http methods supported by Kotless Web application */
enum class HttpMethod {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    HEAD,
    OPTIONS
}

/** Mime types supported by StaticResource */
enum class MimeType(val mimeText: String, val isBinary: Boolean, val extension: String) {
    PLAIN("text/plain", false, "txt"),
    MARKDOWN("text/markdown", false, "md"),
    HTML("text/html", false, "html"),
    CSS("text/css", false, "css"),

    PNG("image/png", true, "png"),
    APNG("image/apng", true, "apng"),
    GIF("image/gif", true, "gif"),
    SVG("image/svg", true, "svg"),
    JPEG("image/jpeg", true, "jpeg"),
    BMP("image/bmp", true, "bmp"),
    WEBP("image/webp", true, "webp"),
    TTF("font/ttf", true, "ttf"),

    JS("application/javascript", false, "js"),
    JSMAP("application/json", false, "map"),
    JSON("application/json", false, "json"),
    XML("application/xml", false, "xml"),
    ZIP("application/zip", true, "zip"),
    GZIP("application/gzip", true, "gzip");

    companion object {
        fun binary() = values().filter { it.isBinary }.toTypedArray()
        fun forDeclaration(type: String, subtype: String) = values().find { "${type}/${subtype}" == it.mimeText }
        fun forFile(file: File) = values().find { it.extension == file.extension }
    }
}
