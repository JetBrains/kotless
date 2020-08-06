package io.kotless.plugin.gradle

import io.kotless.plugin.gradle.utils.Resources
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class ExamplesTerraformTests {
    companion object {
        private fun actualPrefix(project: String) = "build/$project/kotless-gen/deploy"
        private const val expectedPrefix = "/examples"

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
        val task = "$dsl:$project:generate"
        val absolutePath = File("../../examples")

        val runner = GradleRunner
            .create()
            .withDebug(true)
            .withProjectDir(absolutePath)

        runner.withArguments(task).build()

        val actual = File(runner.projectDir, "${actualPrefix(project)}/$project.tf")
        val expected = Resources.read("$expectedPrefix/$dsl/$project.tf").replace("{root}", absolutePath.canonicalPath)

        Assertions.assertEquals(expected, actual.readText())
    }

}
