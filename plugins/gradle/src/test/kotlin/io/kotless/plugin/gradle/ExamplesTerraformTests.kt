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

        private val time = IntRange(250, 5000)

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

        val runner = GradleRunner
            .create()
            .withDebug(true)
            .withProjectDir(File("../../examples"))

        runner.withArguments(task).build()

        val actual = File(runner.projectDir, "${actualPrefix(project)}/$project.tf")
        val expected = "$expectedPrefix/$dsl/$project.tf"

        val resources = File("/home/tanvd/work/kotless/plugins/gradle/src/test/resources")

//        val toWrite = File(resources, expected)
//        toWrite.parentFile.mkdirs()
//
//        toWrite.writeText(actual.readText())
//
        Assertions.assertEquals(Resources.read(expected), actual.readText())
    }

}
