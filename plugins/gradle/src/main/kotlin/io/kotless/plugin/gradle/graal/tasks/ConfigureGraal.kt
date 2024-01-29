package io.kotless.plugin.gradle.graal.tasks

import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
import com.bmuschko.gradle.docker.tasks.container.DockerLogsContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStartContainer
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.kotless.plugin.gradle.graal.dsl.runtime
import io.kotless.plugin.gradle.graal.utils.GraalSettings
import io.kotless.plugin.gradle.graal.utils.Groups
import org.codehaus.plexus.util.Os
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Property
import org.gradle.api.tasks.bundling.Zip
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermissions
import java.util.*


internal object ConfigureGraal {

    internal fun setupGraalVMTasks(project: Project, shadowJar: ShadowJar) {
        if (project.runtime.handler?.split("::")?.size != 2) {
            project.logger.warn("Kotlin GraalVM Runtime requires correct `handler`. The field should be set via `runtime` extension`.")
            return
        }
        createGraalVmRuntimeTasks(project, shadowJar)
    }

    private fun createGraalVmRuntimeTasks(project: Project, shadowJar: ShadowJar) {
        with(project) {
            val outputDirectory = File(buildDir, "native")
            val nativeFileName = shadowJar.archiveFile.get().asFile.nameWithoutExtension
            val dockerfile = createDockerfile(shadowJar.archiveFile.get().asFile)
            val nativeImage = createNativeImage(dockerfile)
            val nativeContainer = createNativeContainer(shadowJar, nativeImage)
            val logs = createLogsContainer(nativeContainer.containerId)
            val startContainer = createStartContainer(nativeContainer, logs)
            val nativeBuild = createNativeBuild(shadowJar, startContainer)

            initGraalRuntimeTask(nativeBuild, nativeFileName, outputDirectory)
        }
    }

    private fun Project.initGraalRuntimeTask(nativeBuild: Task, nativeFileName: String, outputDirectory: File) {
        tasks.create("buildGraalRuntime", Zip::class.java) {
            it.group = Groups.graal
            it.dependsOn(nativeBuild)
            it.from(outputDirectory)
            it.from(generateBootstrap(buildDir, nativeFileName, runtime.config.memoryMb))
        }
    }

    private fun Project.createDockerfile(file: File): Dockerfile {
        val jarFileName = file.name
        val nativeFileName = file.nameWithoutExtension
        return tasks.create("createDockerfile", Dockerfile::class.java) { dockerfile ->
            generateReflect(buildDir)
            val graalImage = runtime.config.getImageOrDefault()
            dockerfile.group = Groups.`graal setup`
            dockerfile.from(graalImage)
            dockerfile.instruction("RUN mkdir -p /working/build")

            runtime.config.dockerAdditionalInstructions?.forEach { instruction ->
                dockerfile.instruction(instruction)
            }

            dockerfile.entryPoint("bash")
            project.afterEvaluate {
                val flags = runtime.config.getFlagsOrDefault().joinToString(" ")
                dockerfile.defaultCommand(
                    "-c",
                    runtime.config.dockerBuildCommand ?: """
                        ls /working/build/libs; \
                        native-image $flags /working/build/libs/$jarFileName; \
                        mkdir -p /working/build/native; \
                        cp -f $nativeFileName /working/build/native/$nativeFileName; \
                        chmod -R 777 /working/build/native; \
                        chmod +x /working/build/native/$nativeFileName
                    """.trimIndent()
                )
            }
        }
    }


    private fun Project.createNativeImage(dockerfile: Dockerfile): DockerBuildImage {
        return tasks.create("buildGraalImage", DockerBuildImage::class.java) {
            it.group = Groups.`graal setup`
            it.dependsOn(dockerfile)
            it.images.add("kotlin/graal-native-build:latest")
        }
    }


    private fun Project.createNativeContainer(shadowJar: ShadowJar, nativeImage: DockerBuildImage): DockerCreateContainer {
        return tasks.create("createGraalContainer", DockerCreateContainer::class.java) {
            it.group = Groups.`graal setup`

            val buildDir = osPath(runtime.config.dockerBuildDirOverride ?: buildDir.absolutePath)
            val dockerVolumesBind =
                (runtime.config.dockerVolumesBind ?: emptyMap()).map { entry -> osPath(entry.key) to entry.value }

            it.dependsOn(shadowJar, nativeImage)
            it.targetImageId(nativeImage.imageId)
            it.hostConfig.autoRemove.set(true)
            it.hostConfig.binds.set(mapOf(buildDir to "/working/build") + dockerVolumesBind)
        }
    }


    private fun Project.createLogsContainer(containerId: Property<String>): DockerLogsContainer {
        return tasks.create("graalContainerLogs", DockerLogsContainer::class.java) { logsContainer ->
            logsContainer.group = Groups.`graal setup`
            logsContainer.targetContainerId(containerId)
            logsContainer.follow.set(true)
            logsContainer.tailAll.set(true)
            logsContainer.onNext {
                // Each log message from the container will be passed as it's made available
                logger.quiet(it.toString())
            }
        }
    }

    private fun Project.createStartContainer(
        nativeContainer: DockerCreateContainer,
        logs: DockerLogsContainer
    ): DockerStartContainer {
        return tasks.create("startGraalContainer", DockerStartContainer::class.java) {
            it.group = Groups.`graal setup`
            it.dependsOn(nativeContainer)
            it.targetContainerId(nativeContainer.containerId)
            it.finalizedBy(logs)
        }
    }

    private fun Project.createNativeBuild(shadowJar: ShadowJar, startContainer: DockerStartContainer): Task {
        return tasks.create("buildGraalExecutable") {
            it.group = Groups.`graal setup`
            it.dependsOn(shadowJar, startContainer)
        }
    }

    private fun generateBootstrap(buildDir: File, nativeFileName: String, memoryMb: Int): File {
        val file = File(buildDir, "bootstrap")
        file.delete()

        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            Files.createFile(file.toPath())
        } else {
            val posix = PosixFilePermissions.fromString("rwxr-xr-x")
            Files.createFile(file.toPath(), PosixFilePermissions.asFileAttribute(posix))
        }

        file.parentFile.mkdirs()
        file.createNewFile()
        //language=sh
        file.writeText(
            """
            #!/bin/sh
            set -euo pipefail
            ./${nativeFileName} -Xmx${memoryMb}m
            """.trimIndent()
        )
        return file
    }

    private fun Project.generateReflect(buildDir: File): File {
        val content = runtime.config.reflectConfiguration ?: ConfigureGraal::class.java
            .getResource("/${GraalSettings.DEFAULT_REFLECT_FILE_NAME}")
            .readText()
        val file = File(buildDir, GraalSettings.DEFAULT_REFLECT_FILE_NAME)
        file.parentFile.mkdirs()
        file.createNewFile()
        file.writeText(content)
        return file
    }

    private fun osPath(path: String): String {
        return when {
            Os.isFamily(Os.FAMILY_WINDOWS) -> {
                val drive = path.substring(0, path.indexOf(':'))

                path
                    .replace('\\', '/')
                    .replace("$drive:", "/${drive.lowercase(Locale.getDefault())}", ignoreCase = true)
            }

            else -> path
        }
    }
}
