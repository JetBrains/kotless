package io.kotless.plugin.gradle

import io.kotless.DSLType
import io.kotless.parser.LocalParser
import io.kotless.plugin.gradle.dsl.*
import io.kotless.plugin.gradle.graal.RuntimeKotlinGradlePlugin
import io.kotless.plugin.gradle.graal.dsl.runtime
import io.kotless.plugin.gradle.graal.tasks.GenerateAdapter
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

        val dsl = Dependencies.dsl(project)
        val (_, dependency) = dsl.entries.single()
        val version = dependency.version ?: error("Explicit version is required for Kotless DSL dependency.")

        dependencies {
            myImplementation(
                "io.kotless",
                "graal-runtime",
                version
            )
        }

        RuntimeKotlinGradlePlugin().apply(project)

        val qualifiedName = LocalParser.parse(project.myKtSourceSet.toSet(), Dependencies.getDependencies(project)).entrypoint.qualifiedName
        val mainClass = qualifiedName.split("::")[0]

        val homePath = getHomePath()

        //for caching
        val graalGradle = File(buildDir.parentFile.parentFile.parent, "build/graal-gradle")

        runtime {
            handler = qualifiedName
            classAnnotations = "@OptIn(io.kotless.InternalAPI::class)"
            config {
                image = "ghcr.io/graalvm/graalvm-community:21"
                flags = kotless.webapp.graal.buildArgs

                if (kotless.config.dsl.typeOrDefault == DSLType.SpringBoot) {
                    additionalSources = getAdditionalResources(kotless, mainClass)
                    dockerBuildDirOverride = buildDir.parentFile.parentFile.parent
                    dockerVolumesBind = mapOf(
                        getM2RepoPath(homePath) to "/root/.m2/repository",
                        graalGradle.absolutePath to "/root/.gradle/"
                    ) + (kotless.webapp.graal.buildImageAdditionalBinds ?: emptyList()).map { file ->
                        file.absolutePath to "/app/build/${file.name}"
                    }
                    dockerAdditionalInstructions = getDockerAdditionalInstructions(kotless)
                    dockerBuildCommand = getDockerBuildCommand(project.path, kotless)
                }
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

            val graalBuildArgs = kotless.webapp.graal.buildArgs

            if (graalBuildArgs != null) {
                graalVmExtensionBinaries.buildArgs(graalBuildArgs)
            }
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
                    data = MainSource.data(mainClass, kotless.webapp.graal.validationMainPackage),
                    type = MainSource.type
                ),
                GenerateAdapter.Source(
                    filePath = AotFactoriesResourceSource.filePath,
                    data = AotFactoriesResourceSource.data,
                    type = AotFactoriesResourceSource.type
                ),
                GenerateAdapter.Source(
                    filePath = RuntimeHintsSource.filePath,
                    data = RuntimeHintsSource.data(
                        kotless.webapp.graal.apiPackages,
                        kotless.webapp.graal.modelPackages
                    ),
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
                "RUN microdnf install -y rsync",
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
            val env = kotless.webapp.lambda.environment.entries.joinToString(" ") { (key, value) -> "export $key=$value;" }

            return """
                rm -rf /root/.gradle/daemon; \             
                mkdir -p /app/build; \
                rsync -a --exclude=build /working/build/kotless /app/build; \
                cp /working/build/build.gradle.kts /app/build; \
                cp /working/build/settings.gradle.kts /app/build; \
                cd /app/build; \
                $env gradle $projectPath:nativeCompile; \
                rm -rf $nativeFolderDest; \
                mkdir -p $nativeFolderDest; \
                cp -rp /app/build$normalizedProjectPath/build/native/nativeCompile/* $nativeFolderDest; \
                mv $nativeFolderDest/$projectName $nativeFileDest; \
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
