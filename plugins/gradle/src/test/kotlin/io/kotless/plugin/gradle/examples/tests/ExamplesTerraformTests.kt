package io.kotless.plugin.gradle.examples.tests

import io.kotless.plugin.gradle.examples.ExamplesTestBase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class ExamplesTerraformTests : ExamplesTestBase() {
    companion object {
        @JvmStatic
        fun data() = ExamplesTestBase.data()
    }


    @Tag("integration-ci")
    @MethodSource("data")
    @ParameterizedTest(name = "task {0} in ms range {1}")
    fun `test generate time site example`(dsl: String, project: String) {
        execute(dsl, project, "generate")

        Assertions.assertEquals(expected(dsl, project), actual(project).readText())
    }

}
