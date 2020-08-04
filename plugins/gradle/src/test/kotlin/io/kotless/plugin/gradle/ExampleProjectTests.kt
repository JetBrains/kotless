package io.kotless.plugin.gradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import kotlin.system.measureTimeMillis

@RunWith(Parameterized::class)
class ExampleProjectTests(val task: String, val time: IntRange) {
    companion object {
        private val time = IntRange(500, 3000)

        @JvmStatic
        @Parameterized.Parameters(name = "{0}: {1}")
        fun data() = listOf(
            arrayOf("kotless:site:generate", time),
            arrayOf("kotless:shortener:generate", time),

            arrayOf("ktor:site:generate", time),
            arrayOf("ktor:shortener:generate", time),

            arrayOf("spring:site:generate", time),
            arrayOf("spring:shortener:generate", time)
        )
    }


    @Test
    fun `test generate time site example`() {
        val runner = GradleRunner
            .create()
            .withDebug(true)
            .withProjectDir(File("../../examples"))

        runner.withArguments("build").build()

        val total = measureTimeMillis {
            runner.withArguments(task).build()
        }

        println(total)

        Assert.assertTrue(total in time)
    }

}
