package io.kotless.dsl.lang.http

import io.kotless.MimeType


/**
 * Get route of Kotless web application (dynamic function).
 *
 * Deserialization of it's params, if it is primitive types, will be done automatically.
 *
 * Serialization of the result also will be done automatically. Nevertheless, it is
 * possible to return data as HttpResponse -- in this case no serialization will be performed.
 *
 * @param path -- URI path for this route.
 * @param mime -- MimeType of payload.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Get(val path: String, val mime: MimeType = MimeType.HTML)

/**
 * Post route of Kotless web application (dynamic function)
 *
 * Deserialization of it's params, if it is primitive types, will be done automatically.
 *
 * Serialization of the result also will be done automatically. Nevertheless, it is
 * possible to return data as HttpResponse -- in this case no serialization will be performed.
 *
 * @see io.kotless.dsl.model.HttpResponse
 *
 * @param path -- URI path for this route.
 * @param mime -- MimeType of payload.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Post(val path: String, val mime: MimeType = MimeType.JSON)


/**
 * Put route of Kotless web application (dynamic function).
 *
 * Deserialization of it's params, if it is primitive types, will be done automatically.
 *
 * Serialization of the result also will be done automatically. Nevertheless, it is
 * possible to return data as HttpResponse -- in this case no serialization will be performed.
 *
 * @param path -- URI path for this route.
 * @param mime -- MimeType of payload.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Put(val path: String, val mime: MimeType = MimeType.JSON)

/**
 * Patch route of Kotless web application (dynamic function).
 *
 * Deserialization of it's params, if it is primitive types, will be done automatically.
 *
 * Serialization of the result also will be done automatically. Nevertheless, it is
 * possible to return data as HttpResponse -- in this case no serialization will be performed.
 *
 * @param path -- URI path for this route.
 * @param mime -- MimeType of payload.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Patch(val path: String, val mime: MimeType = MimeType.JSON)

/**
 * Delete route of Kotless web application (dynamic function).
 *
 * Deserialization of it's params, if it is primitive types, will be done automatically.
 *
 * Serialization of the result also will be done automatically. Nevertheless, it is
 * possible to return data as HttpResponse -- in this case no serialization will be performed.
 *
 * @param path -- URI path for this route.
 * @param mime -- MimeType of payload.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Delete(val path: String, val mime: MimeType = MimeType.JSON)

/**
 * Head route of Kotless web application (dynamic function).
 *
 * Deserialization of it's params, if it is primitive types, will be done automatically.
 *
 * Serialization of the result also will be done automatically. Nevertheless, it is
 * possible to return data as HttpResponse -- in this case no serialization will be performed.
 *
 * @param path -- URI path for this route.
 * @param mime -- MimeType of payload.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Head(val path: String, val mime: MimeType = MimeType.JSON)

/**
 * Options route of Kotless web application (dynamic function).
 *
 * Deserialization of it's params, if it is primitive types, will be done automatically.
 *
 * Serialization of the result also will be done automatically. Nevertheless, it is
 * possible to return data as HttpResponse -- in this case no serialization will be performed.
 *
 * @param path -- URI path for this route.
 * @param mime -- MimeType of payload.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Options(val path: String, val mime: MimeType = MimeType.JSON)


/**
 * Static get route of Kotless web application (static resources).
 *
 * **This annotation has severe limitations on an annotated object:**
 *
 * Under annotation should be a property, which value is instantiated
 * directly with `java.io.File(String)` constructor and path should
 * be relative to working dir of Kotless.
 *
 * For example:
 *
 * ```
 * @StaticGet("/my_file.css", MimeType.CSS)
 * val a = File("src/resources/my_file.css")
 * ```
 *
 * @param path -- URI path for this static file.
 * @param mime -- MimeType of payload.
 */
@Target(AnnotationTarget.FIELD)
annotation class StaticGet(val path: String, val mime: MimeType)

