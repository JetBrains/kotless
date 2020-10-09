package io.kotless.plugin.gradle.examples.tests

import io.kotless.plugin.gradle.examples.ExamplesTestBase
import io.kotless.plugin.gradle.utils.CommandLine
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class ExamplesValidateTests: ExamplesTestBase() {
    companion object {
        @JvmStatic
        fun dataWithoutGraal() = ExamplesTestBase.dataWithoutGraal()
    }


    @Tag("integration-local")
    @MethodSource("dataWithoutGraal")
    @ParameterizedTest(name = "task {0} in ms range {1}")
    fun `test generate time site example`(dsl: String, project: String) {
        execute("download_terraform")
        execute(dsl, project, "generate")
        execute(dsl, project, "shadowJar")

        val actual = actual(project).parentFile

        CommandLine.execute(
            exec = terraform(project).canonicalPath,
            args = listOf("init","-backend=false"),
            workingDir = actual,
            redirectStdout = true,
            redirectErr = true
        )

        val status = CommandLine.execute(
            exec = terraform(project).canonicalPath,
            args = listOf("validate"),
            workingDir = actual,
            redirectStdout = true,
            redirectErr = true
        )

        Assertions.assertEquals(0, status)
    }

}
