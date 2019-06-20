package io.kotless.plugin.gradle.tasks

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
open class TerraformOperation : DefaultTask() {
    init {
        group = "kotless"
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
    val root: File
        get() = project.kotless.kotlessConfig.genDirectory

    @TaskAction
    fun execute() {
        CommandLine.execute(TerraformDownload.tfBin(project).absolutePath, operation.op, root, redirectStdout = true, redirectErr = true)
    }
}
