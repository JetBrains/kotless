package io.kotless.plugin.gradle.dsl

import io.kotless.InternalAPI
import io.kotless.dsl.config.KotlessAppConfig
import org.gradle.api.Project
import java.io.Serializable

/**
 * Kotless web application
 * It includes ApiGateway REST API definition and Route53 alias with SSL certificate, if present.
 */
@KotlessDSLTag
class Webapp(project: Project) : Serializable {
    @KotlessDSLTag
    class Lambda(project: Project) : Serializable {
        /** Memory in megabytes available for a lambda */
        var memoryMb: Int = 1024

        /** Limit of lambda execution in seconds */
        var timeoutSec: Int = 300

        val environment: HashMap<String, String> = HashMap()

        @OptIn(InternalAPI::class)
        internal val mergedEnvironment: Map<String, String>
            get() = environment + mapOf(KotlessAppConfig.PACKAGE_ENV_NAME to kotlessDSL.packages.joinToString(separator = ","))

        @KotlessDSLTag
        class KotlessDSLRuntime(project: Project) : Serializable {
            /** Default value is the group of project */
            var packages: Set<String> = setOf(project.group.toString())
        }

        private val kotlessDSL = KotlessDSLRuntime(project)

        /** Setup configuration for Kotless DSL */
        @KotlessDSLTag
        fun kotless(configure: KotlessDSLRuntime.() -> Unit) {
            kotlessDSL.configure()
        }
    }

    internal val lambda: Lambda = Lambda(project)

    /** Optimizations applied during generation */
    @KotlessDSLTag
    fun lambda(configure: Lambda.() -> Unit) {
        lambda.configure()
    }

    /** Deployment definition of ApiGateway. Recreated each redeploy. */
    @KotlessDSLTag
    class Deployment : Serializable {
        /**
         * A unique name of deployment
         * By default it is `projectName`
         * (in case of unnamed rootProject -- `root`)
         */
        var name: String? = null

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
        deployment.configure()
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
