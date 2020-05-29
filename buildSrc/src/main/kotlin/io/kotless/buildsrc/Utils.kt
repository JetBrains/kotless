package io.kotless.buildsrc

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

fun Project.optInInternalAPI() {
    tasks.myWithType<KotlinJvmCompile> {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + listOf("-Xuse-experimental=io.kotless.InternalAPI")
        }
    }
}
