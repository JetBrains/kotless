package io.kotless.plugin.gradle.graal.dsl

import io.kotless.plugin.gradle.graal.tasks.GenerateAdapter
import io.kotless.plugin.gradle.graal.utils.GraalSettings
import org.gradle.api.Project
import java.io.File
import java.io.Serializable

@DslMarker
annotation class RuntimeDSLTag

class RuntimePluginExtension : Serializable {
    var handler: String? = null
    var classAnnotations: String? = null
    var additionalSources: List<GenerateAdapter.Source>? = null
    var generationPath: File? = null

    internal fun generationPathOrDefault(project: Project): File {
        if (generationPath != null) return generationPath!!
        val default = File(project.buildDir, "aws-graal-gen/src/main")
        default.mkdirs()
        return default
    }

    internal fun kotlinGenerationPathOrDefault(project: Project): File {
        val kotlin = File(generationPathOrDefault(project), "kotlin")
        kotlin.mkdirs()
        return kotlin
    }

    internal fun javaGenerationPathOrDefault(project: Project): File {
        val kotlin = File(generationPathOrDefault(project), "java")
        kotlin.mkdirs()
        return kotlin
    }

    internal fun resourcesGenerationPathOrDefault(project: Project): File {
        val kotlin = File(generationPathOrDefault(project), "resources")
        kotlin.mkdirs()
        return kotlin
    }

    internal val config: RuntimeConfig = RuntimeConfig()

    @RuntimeDSLTag
    fun config(configure: RuntimeConfig.() -> Unit) {
        config.configure()
    }

    @RuntimeDSLTag
    class RuntimeConfig : Serializable {
        var reflectConfiguration: String? = null
        var flags: List<String>? = null
        var useFullFlgas: Boolean = false
        var image: String? = null
        var dockerBuildDirOverride: String? = null
        var dockerVolumesBind: Map<String, String>? = null
        var dockerAdditionalInstructions: List<String>? = null
        var dockerBuildCommand: String? = null

        internal fun getFlagsOrDefault(): List<String> {
            val projectFlags = flags
            return if (projectFlags == null) {
                GraalSettings.FULL_GRAAL_VM_FLAGS
            } else {
                projectFlags + if (useFullFlgas) GraalSettings.FULL_GRAAL_VM_FLAGS else GraalSettings.BASE_GRAAL_FLAGS
            }
        }

        internal fun getImageOrDefault() = image ?: GraalSettings.GRAAL_VM_DOCKER_IMAGE
    }

}
