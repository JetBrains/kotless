package io.kotless.dsl.app.http

import io.kotless.HttpMethod
import io.kotless.URIPath

/** Descriptor of HTTP route. */
data class RouteKey(val method: HttpMethod, val path: URIPath) {
    constructor(method: HttpMethod, path: String) : this(method, URIPath(path.split("/")))
}
