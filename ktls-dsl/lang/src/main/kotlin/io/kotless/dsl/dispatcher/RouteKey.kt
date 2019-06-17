package io.kotless.dsl.dispatcher

import io.kotless.*

/** Descriptor of HTTP route. */
data class RouteKey(val method: HttpMethod, val mimeType: MimeType, val path: URIPath) {
    constructor(method: HttpMethod, mimeType: MimeType, path: String) : this(method, mimeType, URIPath(path.split("/")))
}
