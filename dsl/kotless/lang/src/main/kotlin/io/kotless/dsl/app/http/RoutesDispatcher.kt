package io.kotless.dsl.app.http

import io.kotless.dsl.lang.KotlessContext
import io.kotless.dsl.lang.http.*
import io.kotless.dsl.model.HttpRequest
import io.kotless.dsl.model.HttpResponse
import io.kotless.dsl.reflection.FunctionCaller
import io.kotless.dsl.reflection.ReflectionScanner
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException

internal object RoutesDispatcher {
    private val logger = LoggerFactory.getLogger(RoutesDispatcher::class.java)

    private val pipeline by lazy { preparePipeline(ReflectionScanner.objectsWithSubtype<HttpRequestInterceptor>().sortedBy { it.priority }) }

    fun dispatch(request: HttpRequest, resourceKey: RouteKey): HttpResponse {
        return try {
            KotlessContext.HTTP.request = request
            pipeline(request, resourceKey)
        } finally {
            KotlessContext.HTTP.reset()
        }
    }

    private fun preparePipeline(left: List<HttpRequestInterceptor>): (HttpRequest, RouteKey) -> HttpResponse {
        if (left.isNotEmpty()) {
            val interceptor = left.first()
            return { req, key ->
                interceptor.intercept(req, key, preparePipeline(left.drop(1)))
            }
        } else {
            return { req, key -> processRequest(req, key) }
        }
    }

    private fun processRequest(request: HttpRequest, resourceKey: RouteKey): HttpResponse {
        logger.info("Passing request to route {}", resourceKey)

        val (func, mime) = RoutesStorage[resourceKey] ?: return notFound()
        logger.debug("Found $func for key $resourceKey")

        val result = try {
            FunctionCaller.call(func, request.params.orEmpty())
        } catch (e: Exception) {
            logger.error("Failed on call of function ${func.name}", if (e is InvocationTargetException) e.targetException else e)
            return serverError(e.message)
        }

        logger.info("Route returned result")
        return when (result) {
            is HttpResponse -> result
            else -> okResponse(result?.toString(), mime)
        }
    }
}
