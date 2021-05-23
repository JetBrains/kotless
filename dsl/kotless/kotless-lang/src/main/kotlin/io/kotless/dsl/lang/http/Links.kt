package io.kotless.dsl.lang.http

import io.kotless.URIPath
import io.kotless.dsl.conversion.ConversionService
import io.kotless.toURIPath
import java.io.File
import java.net.URI
import java.net.URLEncoder
import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaField

/** Get path of the static route. */
val KProperty0<File>.href
    get() = this.javaField!!.getAnnotation(StaticGet::class.java).path

/** Get path of the dynamic route. */
val KFunction<*>.href
    get() = (this.findAnnotation<Get>()?.path
        ?: this.findAnnotation<Post>()?.path
        ?: this.findAnnotation<Put>()?.path
        ?: this.findAnnotation<Patch>()?.path
        ?: this.findAnnotation<Delete>()?.path
        ?: this.findAnnotation<Head>()?.path
        ?: this.findAnnotation<Options>()?.path!!).toAbsoluteHref()

fun Function0<*>.href() = (this as KFunction<*>).href

private fun String.toAbsoluteHref(): String {
//    val stagePath = KotlessContext.HTTP.request.requestContext.stagePath.toURIPath()
    val stagePath = URIPath()
    val hrefPath = this.toURIPath()
    return URIPath(stagePath, hrefPath).toAbsoluteString()
}

fun <T1> Function1<T1, *>.href(va1: T1): String {
    return this.href.params(valueParamsNames.zip(listOf(va1)).filterNotNull())
}

fun <T1, T2> Function2<T1, T2, *>.href(val1: T1, val2: T2): String {
    return this.href.params(valueParamsNames.zip(listOf(val1, val2)).filterNotNull())
}

fun <T1, T2, T3> Function3<T1, T2, T3, *>.href(val1: T1, val2: T2, val3: T3): String {
    return this.href.params(valueParamsNames.zip(listOf(val1, val2, val3)).filterNotNull())
}

fun <T1, T2, T3, T4> Function4<T1, T2, T3, T4, *>.href(val1: T1, val2: T2, val3: T3, val4: T4): String {
    return this.href.params(valueParamsNames.zip(listOf(val1, val2, val3, val4)).filterNotNull())
}

fun <T1, T2, T3, T4, T5> Function5<T1, T2, T3, T4, T5, *>.href(val1: T1, val2: T2, val3: T3, val4: T4, val5: T5): String {
    return this.href.params(valueParamsNames.zip(listOf(val1, val2, val3, val4, val5)).filterNotNull())
}

fun <T1, T2, T3, T4, T5, T6> Function6<T1, T2, T3, T4, T5, T6, *>.href(val1: T1, val2: T2, val3: T3, val4: T4, val5: T5, val6: T6): String {
    return this.href.params(valueParamsNames.zip(listOf(val1, val2, val3, val4, val5, val6)).filterNotNull())
}

fun <T1, T2, T3, T4, T5, T6, T7> Function7<T1, T2, T3, T4, T5, T6, T7, *>.href(val1: T1, val2: T2, val3: T3, val4: T4, val5: T5, val6: T6, val7: T7): String {
    return this.href.params(valueParamsNames.zip(listOf(val1, val2, val3, val4, val5, val6, val7)).filterNotNull())
}

private fun List<Pair<String, Any?>>.filterNotNull() = filter { it.second != null }.map { it.first to it.second as Any }

private val Function<*>.href
    get() = (this as KFunction<*>).href

private val Function<*>.valueParams
    get() = (this as KFunction<*>).parameters.filter { it.kind == KParameter.Kind.VALUE }

private val Function<*>.valueParamsNames
    get() = valueParams.map { it.name!! }

private fun String.params(params: List<Pair<String, Any>>): String {
    var result = this
    for ((name, value) in params) {
        result = result.param(name, ConversionService.convertTo(value))
    }
    return result
}

/** Add URL encoded param to the path */
private fun String.param(name: String, value: String): String {
    val uri = URI(this)

    val queryParams = buildString {
        append(uri.query.orEmpty())
        if (isNotEmpty()) {
            append('&')
        }
        append(URLEncoder.encode(name, "UTF-8"))
        append("=")
        append(URLEncoder.encode(value, "UTF-8"))
    }

    return URI(uri.scheme, uri.authority, uri.path, queryParams, uri.fragment).toString()
}
