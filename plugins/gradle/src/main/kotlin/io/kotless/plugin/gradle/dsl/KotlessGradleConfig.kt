package io.kotless.plugin.gradle.dsl

import io.kotless.CloudPlatform
import io.kotless.DSLType
import io.kotless.KotlessConfig.Optimization.MergeLambda
import io.kotless.plugin.gradle.utils.gradle.Dependencies
import io.kotless.plugin.gradle.utils.gradle.myShadowJar
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import java.io.File
import java.io.Serializable

/** Configuration of Kotless itself */
@KotlessDSLTag
class KotlessGradleConfig(project: Project) : Serializable {
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


    internal val deployGenDirectory: File
        get() = File(genDirectory, "deploy")

    internal val localGenDirectory: File
        get() = File(genDirectory, "local")

    /** Name of configuration to use as a classpath */
    var configurationName = "compileClasspath"

    internal var myArchiveTask: String = project.myShadowJar().name

    /** Set custom archive task that should be used to pack lambda instead of default ShadowJar */
    fun setArchiveTask(task: AbstractArchiveTask) {
        myArchiveTask = task.name
    }

    @KotlessDSLTag
    class DSLGradle(project: Project) : Serializable {
        private val defaultType by lazy {
            val types = Dependencies.dsl(project).keys
            require(types.isNotEmpty()) {
                """
                |Kotless was unable to determine DSL type of application.
                |Either dependency with one of the DSLs (`kotless-lang`, `ktor-lang`, `spring-boot-lang`) should be added, or DSL should be specified manually.
                |""".trimMargin()
            }
            require(types.size <= 1) {
                """
                |Kotless was unable to determine DSL type of application. 
                |There was more than one DSL dependency (of type `lang`, `ktor-lang`, `spring-boot-lang`) should be added, or DSL should be specified manually.
                |""".trimMargin()
            }

            types.single()
        }

        internal val typeOrDefault: DSLType
            get() = type ?: defaultType

        /** Type of DSL used by Kotless. By default, will be used inferred from used library */
        var type: DSLType? = null

        /** Statics root correctly resolved for DSL */
        internal val resolvedStaticsRoot
            get() = when (typeOrDefault) {
                DSLType.Ktor -> workingRoot
                DSLType.SpringBoot, DSLType.Kotless -> staticsRoot
            }

        /** Working directory of current project */
        private val workingRoot: File = project.projectDir

        /**
         * Directory Kotless considers as root for Static Resources resolving
         *
         * Will be used for Kotless DSL and SpringBoot to search for static resources.
         * For Ktor use `staticRootFolder` field in `static`.
         *
         * By default, it is `src/main/resources`
         */
        var staticsRoot: File = project.projectDir.resolve("src/main/resources")
            set(value) {
                require(typeOrDefault != DSLType.Ktor) {
                    "Statics root cannot be reassigned for Ktor from Gradle. Use `staticRootFolder` field in `static` closure of your application."
                }
                field = value
            }
    }

    internal val dsl: DSLGradle = DSLGradle(project)

    /** Configuration of DSL used by Kotless */
    @KotlessDSLTag
    fun dsl(configure: DSLGradle.() -> Unit) {
        dsl.configure()
    }

    sealed class CloudGradle<S : CloudGradle.StorageGradle, T : CloudGradle.TerraformGradle<*, *>>(val type: CloudPlatform) : Serializable {

        /** Prefix with which all created resources will be prepended */
        var prefix: String = ""

        class Azure : CloudGradle<StorageGradle.AzureBlob, TerraformGradle.Azure>(CloudPlatform.Azure)

        class AWS : CloudGradle<StorageGradle.S3, TerraformGradle.AWS>(CloudPlatform.AWS) {
            lateinit var profile: String
            lateinit var region: String
        }

        @KotlessDSLTag
        sealed class StorageGradle: Serializable {
            class S3 : StorageGradle() {
                lateinit var bucket: String
                var region: String? = null
            }

            class AzureBlob : StorageGradle() {
                lateinit var container: String
                lateinit var storageAccount: String
            }
        }

        internal val storage: S = when (type) {
            CloudPlatform.AWS -> StorageGradle.S3()
            CloudPlatform.Azure -> StorageGradle.AzureBlob()
        } as S

        fun storage(configure: S.() -> Unit) {
            storage.configure()
        }


        @KotlessDSLTag
        sealed class TerraformGradle<B : TerraformGradle.BackendGradle<*>, P : TerraformGradle.ProviderGradle>(
            internal val backend: B, internal val provider: P
        ) : Serializable {

            class AWS(backend: BackendGradle.AWS, provider: ProviderGradle.AWS) : TerraformGradle<BackendGradle.AWS, ProviderGradle.AWS>(backend, provider)
            class Azure(backend: BackendGradle.Azure, provider: ProviderGradle.Azure) :
                TerraformGradle<BackendGradle.Azure, ProviderGradle.Azure>(backend, provider)

            /**
             * Version of Terraform to use.
             * By default, `0.13.7`
             */
            var version: String = "0.13.7"

            sealed class BackendGradle<S : StorageGradle> : Serializable {
                @KotlessDSLTag
                class AWS : BackendGradle<StorageGradle.S3>() {
                    internal var s3: StorageGradle.S3? = null

                    fun s3(configure: StorageGradle.S3.() -> Unit) {
                        s3 = StorageGradle.S3().also(configure)
                    }

                    /**
                     * Path in a bucket to store Terraform state
                     * By default it is `kotless-state/state.tfstate`
                     */
                    var key: String = "kotless-state/state.tfstate"

                    var profile: String? = null
                }

                @KotlessDSLTag
                class Azure : BackendGradle<StorageGradle.AzureBlob>() {
                    internal var blob: StorageGradle.AzureBlob? = null

                    fun blob(configure: StorageGradle.AzureBlob.() -> Unit) {
                        blob = StorageGradle.AzureBlob().also(configure)
                    }

                    /**
                     * Path in a bucket to store Terraform state
                     * By default it is `kotless-state/state.tfstate`
                     */
                    var key: String = "kotless-state/state.tfstate"

                    lateinit var resourceGroup: String
                }
            }

            /** Configuration of Terraform backend */
            @KotlessDSLTag
            fun backend(configure: B.() -> Unit) {
                backend.configure()
            }

            sealed class ProviderGradle : Serializable {
                @KotlessDSLTag
                class AWS : ProviderGradle() {
                    /** Version of AWS provider to use */
                    var version = "2.70.0"

                    var profile: String? = null

                    var region: String? = null
                }

                @KotlessDSLTag
                class Azure : ProviderGradle() {
                    var version = "2.77.0"
                }
            }

            /** Configuration of Terraform AWS provider */
            @KotlessDSLTag
            fun provider(configure: P.() -> Unit) {
                provider.configure()
            }
        }

        internal val terraform = when (type) {
            CloudPlatform.AWS -> TerraformGradle.AWS(TerraformGradle.BackendGradle.AWS(), TerraformGradle.ProviderGradle.AWS())
            CloudPlatform.Azure -> TerraformGradle.Azure(TerraformGradle.BackendGradle.Azure(), TerraformGradle.ProviderGradle.Azure())
        } as T

        @KotlessDSLTag
        fun terraform(configure: T.() -> Unit) {
            terraform.configure()
        }
    }


    var cloud: CloudGradle<*, *>? = null

    @KotlessDSLTag
    fun aws(configure: CloudGradle.AWS.() -> Unit) {
        cloud = CloudGradle.AWS().also(configure)
    }

    @KotlessDSLTag
    fun azure(configure: CloudGradle.Azure.() -> Unit) {
        cloud = CloudGradle.Azure().also(configure)
    }


    @KotlessDSLTag
    class Optimization : Serializable {
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

        /**
         * Optimization defines, if lambdas should be autowarmed and with what schedule
         *
         * Lambdas cannot be autowarmed with interval more than hour, since it has no practical sense
         */
        @KotlessDSLTag
        data class Autowarm(val enable: Boolean, val minutes: Int = 5) : Serializable

        var autowarm: Autowarm = Autowarm(enable = true)
    }

    internal val optimization: Optimization = Optimization()

    /** Optimizations applied during generation */
    @KotlessDSLTag
    fun optimization(configure: Optimization.() -> Unit) {
        optimization.configure()
    }
}
