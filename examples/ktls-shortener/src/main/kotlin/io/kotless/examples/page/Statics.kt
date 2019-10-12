package io.kotless.examples.page

import io.kotless.MimeType
import io.kotless.dsl.lang.http.StaticGet
import java.io.File

@StaticGet("/css/shortener.css", MimeType.CSS)
val siteCss = File("css/shortener.css")

@StaticGet("/js/shortener.js", MimeType.JS)
val siteJs = File("js/shortener.js")

@StaticGet("/favicon.apng", MimeType.APNG)
val faviconIco = File("favicon.apng")
