package io.kotless.plugin.gradle.dsl

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
class KotlessConfig(project: Project) : Serializable {
    /** Name of bucket Kotless will use to store all files */
    var bucket: String = ""

    /** Prefix with which all created resources will be prepended */
    var prefix: String = ""

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
    inner class DSLConfig(project: Project) : Serializable {
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

    internal val dsl: DSLConfig = DSLConfig(project)

    /** Configuration of DSL used by Kotless */
    @KotlessDSLTag
    fun dsl(configure: DSLConfig.() -> Unit) {
        dsl.configure()
    }

    @KotlessDSLTag
    class Terraform : Serializable {
        /**
         * Version of Terraform to use.
         * By default, `0.12.29`
         */
        var version: String = "0.12.29"

        /** AWS profile from a local machine to use for Terraform operations authentication */
        lateinit var profile: String

        /** AWS region in context of which all Terraform operations should be performed */
        lateinit var region: String

        @KotlessDSLTag
        class Backend : Serializable {
            /**
             * Name of bucket, that will be used as Terraform backend storage
             * By default kotless bucket is used.
             */
            var bucket: String? = null

            /**
             * Path in a bucket to store Terraform state
             * By default it is `kotless-state/state.tfstate`
             */
            var key: String = "kotless-state/state.tfstate"

            var profile: String? = null

            var region: String? = null
        }

        internal val backend = Backend()

        /** Configuration of Terraform backend */
        @KotlessDSLTag
        fun backend(configure: Backend.() -> Unit) {
            backend.configure()
        }

        @KotlessDSLTag
        class AWSProvider : Serializable {
            /** Version of AWS provider to use */
            var version = "2.70.0"

            var profile: String? = null

            var region: String? = null

            var logRetentionInDays: Int? = null
        }

        internal val provider = AWSProvider()

        /** Configuration of Terraform AWS provider */
        @KotlessDSLTag
        fun provider(configure: AWSProvider.() -> Unit) {
            provider.configure()
        }
    }

    internal val terraform: Terraform = Terraform()

    /** Configuration of Terraform */
    @KotlessDSLTag
    fun terraform(configure: Terraform.() -> Unit) {
        terraform.configure()
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
        data class Autowarm(val enable: Boolean, val minutes: Int) : Serializable

        var autowarm: Autowarm = Autowarm(true, 5)
    }

    internal val optimization: Optimization = Optimization()

    /** Optimizations applied during generation */
    @KotlessDSLTag
    fun optimization(configure: Optimization.() -> Unit) {
        optimization.configure()
    }
}
