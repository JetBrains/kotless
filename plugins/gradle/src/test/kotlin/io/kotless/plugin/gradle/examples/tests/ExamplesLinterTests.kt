package io.kotless.plugin.gradle.examples.tests

import io.kotless.plugin.gradle.examples.ExamplesTestBase
import io.kotless.plugin.gradle.utils.Linter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class ExamplesLinterTests: ExamplesTestBase() {
    private val bin = File(runner.projectDir, "build/bin/tflint")

    init {
        bin.parentFile.mkdirs()
        Linter.download(bin)
    }

    companion object {
        @JvmStatic
        fun data() = ExamplesTestBase.data()
    }


    @Tag("integration")
    @MethodSource("data")
    @ParameterizedTest(name = "task {0} in ms range {1}")
    fun `test generate time site example`(dsl: String, project: String) {
        execute(dsl, project, "generate")

        val status = Linter.lint(bin, actual(project).parentFile)

        Assertions.assertEquals(0, status)
    }

}
