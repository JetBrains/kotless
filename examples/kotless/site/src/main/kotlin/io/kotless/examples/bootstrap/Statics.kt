@file:Suppress("unused")

package io.kotless.examples.bootstrap

import io.kotless.MimeType
import io.kotless.dsl.lang.http.StaticGet
import java.io.File


@StaticGet("/css/kotless-site.css", MimeType.CSS)
val kotlessSiteCss = File("css/kotless-site.css")

@StaticGet("/css/highlight-style.css", MimeType.CSS)
val highlightCss = File("css/highlight-style.css")

@StaticGet("/js/highlight.pack.js", MimeType.JS)
val highlightJs = File("js/highlight.pack.js")

@StaticGet("/favicon.apng", MimeType.APNG)
val faviconIco = File("favicon.apng")

