package io.kotless.plugin.gradle.dsl

import io.kotless.InternalAPI
import io.kotless.dsl.config.KotlessAppConfig
import io.kotless.resource.Lambda.Config.Runtime
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

        /** Environment that should be additionally passed to lambda */
        var environment: Map<String, String> = HashMap()

        /** Runtime used to start Lambdas. By default, would be equal to the lowest compatible version.  */
        var runtime: Runtime? = null

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


    @KotlessDSLTag
    data class DNS(val alias: String, val zone: String, val certificate: String ) : Serializable

    /** Alias to RestAPI, if present */
    internal var dns: DNS? = null

    /**
     * DNS CNAME alias
     *
     * @param alias name of alias
     * @param zone a qualified name of zone, alias is created in
     * @param certificate a fully qualified name of certificate, for SSL connection
     */
    @KotlessDSLTag
    fun dns(alias: String, zone: String, certificate: String = "$alias.$zone") {
        dns = DNS(alias, zone, certificate)
    }
}
