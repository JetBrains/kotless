package io.kotless

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
 * and CSS files
 *
 * @param bucket name of bucket where file will be placed
 * @param path URI path under which this resource will be located
 * @param content File with actual content of resource
 * @param mime MIME type of content, will be used to set right headers on http response
 */
data class StaticResource(val bucket: String, val path: URIPath, val content: File, val mime: MimeType) : Visitable
