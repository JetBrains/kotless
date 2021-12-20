package io.kotless.dsl.lang.http

import io.kotless.InternalAPI
import io.kotless.dsl.app.http.RouteKey
import io.kotless.dsl.model.AwsEvent
import io.kotless.dsl.model.Response


/**
 * Kotless interceptor of AWS events
 *
 * Implement this interface in kotlin static object and Kotless will
 * pass all AwsEvents requests through this interceptor.
 *
 * If there is more than one interceptor, than all of them will be sorted
 * in ascending order of priority and request will be passed one by one.
 */
interface AwsEventsInterceptor {
    val priority: Int

    @InternalAPI
    fun intercept(request: AwsEvent, key: RouteKey, next: (AwsEvent, RouteKey) -> Response): Response
}
