package io.kotless.plugin.gradle.examples.tests

import io.kotless.plugin.gradle.examples.ExamplesTestBase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.system.measureTimeMillis

class ExamplesPerformanceTests: ExamplesTestBase() {
    companion object {
        private val time = IntRange(250, 5000)

        @JvmStatic
        fun data() = ExamplesTestBase.data()
    }


    @Tag("integration-ci")
    @MethodSource("data")
    @ParameterizedTest(name = "task {0} in ms range {1}")
    fun `test generate time site example`(dsl: String, project: String) {
        execute(dsl, project, "clean")

        var total = 0L
        for (i in 1..3) {
            total = measureTimeMillis {
                execute(dsl, project, "generate")
            }

            if (total in time) break
        }

        Assertions.assertTrue(total in time) { "$total ms not in range $time" }
    }

}
