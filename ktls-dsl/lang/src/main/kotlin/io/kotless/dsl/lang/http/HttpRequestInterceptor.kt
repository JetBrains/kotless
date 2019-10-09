package io.kotless.dsl.lang.http

import io.kotless.dsl.dispatcher.RouteKey
import io.kotless.dsl.model.HttpRequest
import io.kotless.dsl.model.HttpResponse


/**
 * Kotless interceptor of HTTP requests
 *
 * Implement this interface in kotlin static object and Kotless will
 * pass all HTTP requests through this interceptor.
 *
 * If there is more than one interceptor, than all of them will be sorted
 * in ascending order of priority and request will be passed one by one.
 */
interface HttpRequestInterceptor {
    val priority: Int

    fun intercept(request: HttpRequest, key: RouteKey, next: (HttpRequest, RouteKey) -> HttpResponse): HttpResponse
}
