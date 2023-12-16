package io.kotless.resource

import io.kotless.*
import io.kotless.utils.Visitable
import java.io.File

/**
 * Serverless static resource
 *
 * It is a file uploaded to cloud storage.
 *
 * StaticResource is used to serve requests
 * for static content beyond specified URI.
 *
 * For example, it can be used to server JS
 * and CSS files.
 *
 * @param path URI path under which this resource will be located
 * @param file File with actual content of resource
 * @param mime MIME type of content, will be used to set right headers on http response
 */
@OptIn(InternalAPI::class)
data class StaticResource(val path: URIPath, val file: File, val mime: MimeType) : Visitable
