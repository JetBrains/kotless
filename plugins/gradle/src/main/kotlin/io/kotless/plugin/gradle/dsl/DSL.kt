package io.kotless.plugin.gradle.dsl

import io.kotless.KotlessConfig.Optimization.MergeLambda
import io.kotless.plugin.gradle.utils._ext
import io.kotless.plugin.gradle.utils.ext
import org.gradle.api.Project
import java.io.File
import java.io.Serializable

var Project.kotless: KotlessDsl
    get() = this.ext("kotless")
    set(value) {
        this._ext["kotless"] = value
    }

/** Configuration of Kotless application */
fun Project.kotless(configure: KotlessDsl.() -> Unit) {
    kotless = KotlessDsl(project).apply(configure)
}

/** Kotless DSL root */
class KotlessDsl(project: Project) : Serializable {
    internal var kotlessConfig: KotlessConfig = KotlessConfig(project)
    /** Declaration of Kotless configuration itself */
    fun config(configure: KotlessConfig.() -> Unit) {
        kotlessConfig = kotlessConfig.apply(configure)
    }

    /** Configuration of Kotless itself */
    inner class KotlessConfig(project: Project) : Serializable {
        /** Name of bucket Kotless will use to store all files */
        lateinit var bucket: String

        /** Prefix with which all created resources will be prepended */
        var resourcePrefix: String = ""

        /**
         * A local directory Kotless will use to store needed binaries (like terraform)
         * By default it is `${buildDir}/kotless-bin`
         */
        var binDirectory = File(project.buildDir, "kotless-bin")

        /**
         * A local directory Kotless will use to store generated files
         * By default it is `${buildDir}/kotless-gen`
         */
        var genDirectory = File(project.buildDir, "kotless-gen")

        /**
         * Directory Kotless considers as root for File resolving
         * By default it is `projectDir`
         */
        var workDirectory = project.projectDir as File


        val terraform: Terraform = Terraform()
        /** Configuration of Terraform */
        fun terraform(configure: Terraform.() -> Unit) {
            terraform.apply(configure)
        }

        inner class Terraform : Serializable {
            /**
             * Version of Terraform to use.
             * By default, `0.11.13`
             */
            var version: String = "0.11.13"
            /** AWS profile from a local machine to use for Terraform operations authentication */
            lateinit var profile: String
            /** AWS region in context of which all Terraform operations should be performed */
            lateinit var region: String

            val backend = Backend()
            /** Configuration of Terraform backend */
            fun backend(configure: Backend.() -> Unit) {
                backend.apply(configure)
            }

            inner class Backend : Serializable {
                private var _bucket: String? = null
                /**
                 * Name of bucket, that will be used as Terraform backend storage
                 * By default kotless bucket is used.
                 */
                var bucket: String
                    get() = _bucket ?: this@KotlessConfig.bucket
                    set(value) {
                        _bucket = value
                    }

                /**
                 * Path in a bucket to store Terraform state
                 * By default it is `kotless-state/state.tfstate`
                 */
                var key: String = "kotless-state/state.tfstate"
            }

            val provider = AWSProvider()
            /** Configuration of Terraform AWS provider */
            fun provider(configure: AWSProvider.() -> Unit) {
                provider.apply(configure)
            }

            inner class AWSProvider : Serializable {
                /** Version of AWS provider to use */
                var version = "1.60.0"
            }
        }

        val optimization: Optimization = Optimization()
        /** Optimizations applied during generation */
        fun optimization(configure: Optimization.() -> Unit) {
            optimization.apply(configure)
        }

        inner class Optimization : Serializable {
            /**
             * Optimization defines, if different lambdas should be merged into one and when.
             *
             * Basically, lambda serving few endpoints is more likely to be warm.
             *
             * There are 3 levels of merge optimization:
             * * None -- lambdas will never be merged
             * * PerPermissions -- lambdas will be merged, if they have equal permissions
             * * All -- all lambdas in context are merged in one
             */
            var mergeLambda: MergeLambda = MergeLambda.All
        }
    }

    val webapps = ArrayList<Webapp>()
    /** Configuration of Kotless Web application */
    fun webapp(project: Project, configure: Webapp.() -> Unit) {
        webapps.add(Webapp(project).apply(configure))
    }

    /**
     * Kotless web application
     * It includes ApiGateway REST API definition and Route53 alias with SSL certificate, if present.
     */
    inner class Webapp(project: Project) : Serializable {
        private val projectName: String = project.path
        internal fun project(project: Project): Project = project.project(projectName)

        /** Packages that lambda dispatcher should scan for annotated classes */
        lateinit var packages: Set<String>

        val lambda: LambdaConfig = LambdaConfig()
        /** Optimizations applied during generation */
        fun lambda(configure: LambdaConfig.() -> Unit) {
            lambda.apply(configure)
        }

        inner class LambdaConfig : Serializable {
            /** Memory in megabytes available for a lambda */
            var memoryMb: Int = 1024

            /** Limit of lambda execution in seconds */
            var timeoutSec: Int = 300

            /** Should lambdas in this webapp be autowarmed, or not */
            var autowarm: Boolean = true

            /** Period in minutes between warm invocations */
            val autowarmMinutes: Int = 3
        }

        /** Deployment definition of ApiGateway. Recreated each redeploy */
        inner class Deployment : Serializable {
            /**
             * A unique name of deployment
             * By default it is `projectName`
             * (in case of unnamed rootProject -- `root`)
             */
            var name: String = projectName.trim(':').let { if (it.isBlank()) "root" else it.replace(':', '_') }
            /**
             * Version of this deployment.
             * By default, it is `1`
             */
            var version: String = "1"
        }

        val deployment = Deployment()
        /** Deployment resource of ApiGateway */
        fun deployment(configure: Deployment.() -> Unit) {
            deployment.apply(configure)
        }

        /** Alias to RestAPI, if present */
        var route53: Route53? = null

        /** Route53 CNAME alias */
        inner class Route53(
            /** Name of alias */
            val alias: String,
            /** A qualified name of zone, alias is created in */
            val zone: String,
            /** A fully qualified name of certificate, for SSL connection */
            val certificate: String = "$alias.$zone") : Serializable


    }
}

