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
 */
data class StaticResource(
    /** Name of bucket where file will be placed */
    val bucket: String,
    /** URI path under which this resource will be located */
    val path: URIPath,
    /** File with actual content of resource */
    val content: File,
    /** MIME type of content, will be used to set right
     * headers on http response */
    val mime: MimeType) : Visitable
