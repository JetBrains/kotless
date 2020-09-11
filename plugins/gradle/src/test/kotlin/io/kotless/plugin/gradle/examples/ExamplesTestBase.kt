package io.kotless.plugin.gradle.examples

import io.kotless.plugin.gradle.utils.Resources
import io.kotless.terraform.functions.path
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.params.provider.Arguments
import java.io.File

abstract class ExamplesTestBase {
    companion object {
        fun data() = listOf(
            Arguments.of("kotless", "site"),
            Arguments.of("kotless", "shortener"),

            Arguments.of("ktor", "site"),
            Arguments.of("ktor", "shortener"),

            Arguments.of("spring", "site"),
            Arguments.of("spring", "shortener")
        )
    }

    private val projectDir = File("../../examples")
    protected val runner: GradleRunner
        get() = GradleRunner
            .create()
            .withDebug(true)
            .withProjectDir(projectDir)

    fun execute(task: String) {
        runner.withArguments(task).build()
    }

    fun execute(dsl: String, project: String, task: String) {
        runner.withArguments("$dsl:$project:$task").build()
    }

    /** Get terraform binary for this example */
    fun terraform(project: String): File {
        return File(runner.projectDir, "build/$project/kotless-bin/terraform")
    }

    /** Get actual generated file */
    fun actual(project: String): File {
        return File(projectDir, "build/$project/kotless-gen/deploy/$project.tf")
    }

    /** Get expected content of Terraform file */
    private val containsPathStartingWithWindowsDrive = "^.*[A-Z]:\\\\.*/.*\$".toRegex()
    fun expected(dsl: String, project: String): String {
        return Resources.read("/examples/$dsl/$project/$project.tf")
            .replace("{root}", path(projectDir))
            .lines().joinToString("\n") { line ->
                if (line.matches(containsPathStartingWithWindowsDrive)) {
                    line.replace("/", "\\\\")
                } else {
                    line
                }
            }
    }
}
