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
    CSV("text/csv", false, "csv"),
    ICS("text/calendar", false, "ics"),

    PNG("image/png", true, "png"),
    APNG("image/apng", true, "apng"),
    GIF("image/gif", true, "gif"),
    SVG("image/svg+xml", false, "svg"),
    JPEG("image/jpeg", true, "jpeg"),
    HEIC("image/heic", true, "heic"),
    ICO("image/vnd.microsoft.icon", true, "ico"),
    BMP("image/bmp", true, "bmp"),
    MPEG("audio/mpeg",true, "mp3"),
    M4A("audio/mp4", true, "m4a"),
    MP4("video/mp4", true, "mp4"),
    AAC("audio/aac", true, "aac"),
    WEBP("image/webp", true, "webp"),
    WEBA("audio/webm", true, "weba"),
    WEBM("video/webm", true, "webm"),
    TTF("font/ttf", true, "ttf"),
    WOFF("font/woff", true, "woff"),
    WOFF2("font/woff2", true, "woff2"),

    DOC("application/msword",true, "doc"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document",true, "docx"),
    PPT("application/vnd.ms-powerpoint",true,"ppt"),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", true, "pptx"),
    XLS("application/vnd.ms-excel", true, "xls"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", true, "xlsx"),
    PDF("application/pdf", true, "pdf"),
    EPUB("application/epub+zip",true, "epub"),

    JS("application/javascript", false, "js"),
    JSMAP("application/json", false, "map"),
    JSON("application/json", false, "json"),
    XML("application/xml", false, "xml"),
    SHELL("application/x-shellscript", false,"sh"),
    JAR("application/java-archive", true, "jar"),
    ZIP("application/zip", true, "zip"),
    GZIP("application/gzip", true, "gz");

    companion object {
        fun binary() = values().filter { it.isBinary }.toTypedArray()
        fun forDeclaration(type: String, subtype: String) = values().find { "${type}/${subtype}" == it.mimeText }
        fun forFile(file: File) = values().find { it.extension == file.extension }
    }
}
