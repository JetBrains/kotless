package io.kotless.plugin.gradle

import com.kotlin.aws.runtime.dsl.runtime
import com.kotlin.aws.runtime.tasks.GenerateAdapter
import io.kotless.DSLType
import io.kotless.parser.LocalParser
import io.kotless.plugin.gradle.dsl.*
import io.kotless.plugin.gradle.spring.resources.*
import io.kotless.plugin.gradle.utils.gradle.*
import org.codehaus.plexus.util.Os
import org.graalvm.buildtools.gradle.dsl.GraalVMExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPluginConvention
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getPlugin
import org.springframework.boot.gradle.dsl.SpringBootExtension
import java.io.File

object KotlessRuntimeTasks {
    fun Project.setupGraal() {
        if (kotless.config.dsl.typeOrDefault != DSLType.Ktor && kotless.config.dsl.typeOrDefault != DSLType.SpringBoot) {
            project.logger.warn("GraalVM Runtime can be used only with Ktor DSL for now")
            return
        }

        dependencies {
            myImplementation("com.kotlin.aws.runtime", "runtime-graalvm", "0.1.3")
        }

        applyPluginSafely("com.kotlin.aws.runtime")

        val qualifiedName = LocalParser.parse(project.myKtSourceSet.toSet(), Dependencies.getDependencies(project)).entrypoint.qualifiedName
        val mainClass = qualifiedName.split("::")[0]

        val homePath = getHomePath()

        //for caching
        val graalGradle = File(buildDir, "graal-gradle")

        runtime {
            handler = qualifiedName
            classAnnotations = "@OptIn(io.kotless.InternalAPI::class)"
            config {
                image = "ghcr.io/graalvm/graalvm-community:21"
                additionalSources = getAdditionalResources(kotless, mainClass)
                dockerBuildDirOverride = buildDir.parentFile.parentFile.parent
                dockerVolumesBind = mapOf(
                    getM2RepoPath(homePath) to "/root/.m2/repository",
                    graalGradle.absolutePath to "/root/.gradle/"
                )
                dockerAdditionalInstructions = getDockerAdditionalInstructions(kotless)
                dockerBuildCommand = getDockerBuildCommand(project.path, kotless)
            }
        }

        afterEvaluate {
            val graalShadowJar = tasks.getByName("buildGraalRuntime") as AbstractArchiveTask
            kotless.config.setArchiveTask(graalShadowJar)
            tasks.getByName("initialize").dependsOn(graalShadowJar)

            val generateAdapter = tasks.getByName("generateAdapter")
            tasks.getByName("generate").dependsOn(generateAdapter)
        }

        if (kotless.config.dsl.typeOrDefault == DSLType.SpringBoot) {
            applyPluginSafely("org.springframework.boot")
            applyPluginSafely("org.graalvm.buildtools.native")

            convention.getPlugin<ApplicationPluginConvention>().mainClassName = MainSource.className
            extensions.getByType(SpringBootExtension::class.java).mainClass.set(MainSource.className)
            val graalVmExtensionBinaries = extensions.getByType(GraalVMExtension::class.java).binaries.getByName("main")
            graalVmExtensionBinaries.buildArgs(
                "-Dspring.graal.remove-unused-autoconfig=true",
                "-Dspring.graal.remove-yaml-support=true"
            )
        } else {
            convention.getPlugin<ApplicationPluginConvention>().mainClassName = kotless.config.dsl.typeOrDefault.descriptor.localEntryPoint
        }
    }

    private fun getAdditionalResources(kotless: KotlessDSL, mainClass: String): List<GenerateAdapter.Source>? {
        if (kotless.config.dsl.typeOrDefault == DSLType.SpringBoot) {
            return listOf(
                GenerateAdapter.Source(
                    filePath = LambdaContainerHandlerSource.filePath,
                    data = LambdaContainerHandlerSource.data,
                    type = LambdaContainerHandlerSource.type
                ),
                GenerateAdapter.Source(
                    filePath = MainSource.filePath,
                    data = MainSource.data(mainClass),
                    type = MainSource.type
                ),
                GenerateAdapter.Source(
                    filePath = AotFactoriesResourceSource.filePath,
                    data = AotFactoriesResourceSource.data,
                    type = AotFactoriesResourceSource.type
                ),
                GenerateAdapter.Source(
                    filePath = RuntimeHintsSource.filePath,
                    data = RuntimeHintsSource.data(kotless.webapp.lambda.graalModelPackages),
                    type = RuntimeHintsSource.type
                )
            )
        }

        return null
    }

    private fun getDockerAdditionalInstructions(kotless: KotlessDSL): List<String>? {
        if (kotless.config.dsl.typeOrDefault == DSLType.SpringBoot) {
            val path = "PATH=\$PATH:\$GRADLE_HOME/bin"

            return listOf(
                "RUN microdnf install -y wget",
                "RUN microdnf install -y unzip",
                "RUN wget https://services.gradle.org/distributions/gradle-8.5-all.zip",
                "RUN unzip gradle-8.5-all.zip",
                "ENV GRADLE_HOME=/app/gradle-8.5",
                "ENV $path"
            )
        }

        return null
    }

    private fun getDockerBuildCommand(projectPath: String, kotless: KotlessDSL): String? {
        if (kotless.config.dsl.typeOrDefault == DSLType.SpringBoot) {
            val normalizedProjectPath = projectPath.replace(":", "/")
            val projectName = normalizedProjectPath.split("/").last()
            val nativeFolderDest = "/working/build$normalizedProjectPath/build/native"
            val nativeFileDest = "$nativeFolderDest/$projectName-graal"

            return """
                rm -rf /root/.gradle/daemon; \             
                mkdir -p /app/build; \                
                cp -r /working/build/kotless /app/build; \
                cp /working/build/build.gradle.kts /app/build; \
                cp /working/build/settings.gradle.kts /app/build; \
                cd /app/build; \
                gradle $projectPath:nativeCompile; \
                mkdir -p $nativeFolderDest; \
                cp /app/build$normalizedProjectPath/build/native/nativeCompile/$projectName $nativeFileDest; \
                chmod -R 777 $nativeFolderDest; \
                chmod +x $nativeFileDest
            """.trimIndent()
        }

        return null
    }

    private fun getHomePath(): String {
        val env = System.getenv()

        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            val drive = env["HOMEDRIVE"]
            val homePath = env["HOMEPATH"]

            return "$drive$homePath"
        }

        val homePath = env["HOME"]

        return "$homePath"
    }

    private fun getM2RepoPath(homePath: String): String {
        val m2Path = "$homePath/.m2/repository"
        val m2Folder = File(m2Path)

        if (!m2Folder.isDirectory) {
            throw RuntimeException("couldn't locale .m2/repository folder, was looking at: ${m2Folder.absolutePath}")
        }

        return m2Path
    }
}
