package io.kotless.plugin.gradle

import io.kotless.plugin.gradle.utils.CommandLine
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class ExamplesValidateTests {
    companion object {
        private fun actualPrefix(project: String) = "build/$project/kotless-gen/deploy"
        private fun binPrefix(project: String) = "build/$project/kotless-bin/terraform"

        @Suppress("unused")
        @JvmStatic
        fun data() = listOf(
            Arguments.of("kotless", "site"),
            Arguments.of("kotless", "shortener"),

            Arguments.of("ktor", "site"),
            Arguments.of("ktor", "shortener"),

            Arguments.of("spring", "site"),
            Arguments.of("spring", "shortener")
        )
    }


    @Tag("integration")
    @MethodSource("data")
    @ParameterizedTest(name = "task {0} in ms range {1}")
    fun `test generate time site example`(dsl: String, project: String) {
        val absolutePath = File("../../examples")

        val runner = GradleRunner
            .create()
            .withDebug(true)
            .withProjectDir(absolutePath)

        runner.withArguments("download_terraform").build()

        runner.withArguments("$dsl:$project:generate").build()
        runner.withArguments("$dsl:$project:shadowJar").build()

        val actual = File(runner.projectDir, actualPrefix(project))
        val bin = File(runner.projectDir, binPrefix(project))

        CommandLine.execute(
            exec = bin.absolutePath,
            args = listOf("init","-backend=false"),
            workingDir = actual,
            redirectStdout = true,
            redirectErr = true
        )

        val status = CommandLine.execute(
            exec = bin.absolutePath,
            args = listOf("validate"),
            workingDir = actual,
            redirectStdout = true,
            redirectErr = true
        )

        Assertions.assertEquals(0, status)
    }

}
