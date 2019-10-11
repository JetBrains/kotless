package io.kotless.plugin.gradle.dsl

import org.gradle.api.Project
import java.io.Serializable

/**
 * Kotless web application
 * It includes ApiGateway REST API definition and Route53 alias with SSL certificate, if present.
 */
@KotlessDSLTag
class Webapp(project: Project) : Serializable {
    private val projectName: String = project.path
    internal fun project(project: Project): Project = project.project(projectName)

    /** Packages that lambda dispatcher should scan for annotated classes */
    lateinit var packages: Set<String>

    internal val lambda: Lambda = Lambda()
    /** Optimizations applied during generation */
    @KotlessDSLTag
    fun lambda(configure: Lambda.() -> Unit) {
        lambda.apply(configure)
    }

    @KotlessDSLTag
    class Lambda : Serializable {
        /** Memory in megabytes available for a lambda */
        var memoryMb: Int = 1024

        /** Limit of lambda execution in seconds */
        var timeoutSec: Int = 300
    }

    /** Deployment definition of ApiGateway. Recreated each redeploy */
    @KotlessDSLTag
    inner class Deployment : Serializable {
        /**
         * A unique name of deployment
         * By default it is `projectName`
         * (in case of unnamed rootProject -- `root`)
         */
        var name: String = this@Webapp.projectName.trim(':').let { if (it.isBlank()) "root" else it.replace(':', '_') }
        /**
         * Version of this deployment.
         * By default, it is `1`
         */
        var version: String = "1"
    }

    internal val deployment = Deployment()
    /** Deployment resource of ApiGateway */
    @KotlessDSLTag
    fun deployment(configure: Deployment.() -> Unit) {
        deployment.apply(configure)
    }

    /** Alias to RestAPI, if present */
    var route53: Route53? = null

    /**
     * Route53 CNAME alias
     *
     * @param alias name of alias
     * @param zone a qualified name of zone, alias is created in
     * @param certificate a fully qualified name of certificate, for SSL connection
     */
    @KotlessDSLTag
    data class Route53(val alias: String, val zone: String, val certificate: String = "$alias.$zone") : Serializable
}
