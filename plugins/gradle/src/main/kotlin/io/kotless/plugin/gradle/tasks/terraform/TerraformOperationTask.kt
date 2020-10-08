package io.kotless.plugin.gradle.tasks.terraform

import io.kotless.plugin.gradle.dsl.kotless
import io.kotless.plugin.gradle.utils.CommandLine
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File

/**
 * TerraformOperation task executes specified operation on generated terraform code
 *
 * It takes all the configuration from global KotlessDSL configuration (from `kotless` field)
 * and more precisely -- `genDirectory` from it's `kotlessConfig` field.
 *
 * @see kotless
 *
 * Note: Apply will not require approve in a console, kotless passes to it `-auto-approve`
 */
internal open class TerraformOperationTask : DefaultTask() {
    init {
        outputs.upToDateWhen { false }
    }

    enum class Operation(val op: List<String>) {
        INIT(listOf("init")),
        PLAN(listOf("plan")),
        APPLY(listOf("apply", "-auto-approve")),
        DESTROY(listOf("destroy", "-auto-approve"));
    }

    @get:Input
    lateinit var operation: Operation

    @get:InputDirectory
    lateinit var root: File

    @get:Input
    var environment: Map<String, String> = emptyMap()

    @TaskAction
    fun act() {
        CommandLine.execute(
            exec = TerraformDownloadTask.tfBin(project).absolutePath,
            args = operation.op,
            envs = environment,
            workingDir = root,
            redirectStdout = true,
            redirectErr = true
        )
    }
}
