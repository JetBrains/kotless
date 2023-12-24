package io.kotless.plugin.gradle.graal.tasks

import io.kotless.plugin.gradle.graal.dsl.runtime
import io.kotless.plugin.gradle.graal.utils.Groups
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.incremental.deleteDirectoryContents
import java.io.File
import java.io.Serializable

open class GenerateAdapter : DefaultTask() {
    init {
        group = Groups.graal
    }

    @get:Input
    val handler: String?
        get() = project.runtime.handler

    @get:Input
    val classAnnotations: String?
        get() = project.runtime.classAnnotations

    @get:Input
    val additionalSources: List<Source>?
        get() = project.runtime.additionalSources

    @get:OutputDirectory
    val kotlinGenerationPath: File
        get() = project.runtime.kotlinGenerationPathOrDefault(project)

    @get:OutputDirectory
    val javaGenerationPath: File
        get() = project.runtime.javaGenerationPathOrDefault(project)

    @get:OutputDirectory
    val resourcesGenerationPath: File
        get() = project.runtime.resourcesGenerationPathOrDefault(project)

    @TaskAction
    fun act() {
        val handler = handler?.split("::")
        require(handler != null && handler.size == 2) {
            "Kotlin GraalVM Runtime requires correct `handler`." +
                    " The field should be set via `runtime` extension`."
        }
        val (klass, function) = handler

        kotlinGenerationPath.deleteDirectoryContents()
        javaGenerationPath.deleteDirectoryContents()
        resourcesGenerationPath.deleteDirectoryContents()

        val adapter =
            //language=kotlin
            """
                    package io.kotless.graal.aws.runtime
                    
                    import com.amazonaws.services.lambda.runtime.Context
                    import io.kotless.graal.runtime.client.LambdaHTTPClient
                    import org.slf4j.LoggerFactory
                    import java.io.ByteArrayOutputStream

                    val server = ${klass}()

                    ${classAnnotations ?: ""}                   
                    object Adapter {
                        private val log = LoggerFactory.getLogger(Adapter::class.java)
                        fun handleLambdaInvocation(context: Context, apiGatewayProxyRequest: String) {
                            try {
                                val input = apiGatewayProxyRequest.byteInputStream()
                                val output = ByteArrayOutputStream()

                                server.${function}(input, output, context)

                                LambdaHTTPClient.invoke(context.awsRequestId, output.toByteArray())
                            } catch (t: Throwable) {
                                log.error("Invocation error: ", t)
                                LambdaHTTPClient.postInvokeError(context.awsRequestId, t.message)
                            }
                        }
                    }
                """.trimIndent()

        writeSource(Source(filePath = "io/kotless/graal/aws/runtime/Adapter.kt", data = adapter, type = SourceType.Kotlin))

        additionalSources?.forEach(this::writeSource)
    }

    private fun writeSource(source: Source) {
        val generationPath = when(source.type) {
            SourceType.Kotlin -> kotlinGenerationPath
            SourceType.Java -> javaGenerationPath
            SourceType.Resource -> resourcesGenerationPath
        }

        with(File(generationPath, source.filePath)) {
            parentFile.mkdirs()
            writeText(source.data)
        }
    }

    enum class SourceType {
        Kotlin, Java, Resource
    }

    data class Source(val filePath: String, val data: String, val type: SourceType): Serializable
}
