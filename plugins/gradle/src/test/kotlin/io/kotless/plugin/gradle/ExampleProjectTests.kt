package io.kotless.plugin.gradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.system.measureTimeMillis

class ExampleProjectTests {
    companion object {
        private val time = IntRange(500, 3000)

        @JvmStatic
        fun data() = listOf(
            Arguments.of("kotless:site:generate", time),
            Arguments.of("kotless:shortener:generate", time),

            Arguments.of("ktor:site:generate", time),
            Arguments.of("ktor:shortener:generate", time),

            Arguments.of("spring:site:generate", time),
            Arguments.of("spring:shortener:generate", time)
        )
    }


    @MethodSource("data")
    @ParameterizedTest(name = "task {0} in ms range {1}")
    fun `test generate time site example`(task: String, time: IntRange) {
        val runner = GradleRunner
            .create()
            .withDebug(true)
            .withProjectDir(File("../../examples"))

        runner.withArguments("build").build()

        val total = measureTimeMillis {
            runner.withArguments(task).build()
        }

        println(total)

        Assertions.assertTrue(total in time)
    }

}
