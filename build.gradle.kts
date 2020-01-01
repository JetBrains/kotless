import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import tanvd.kosogor.defaults.configureIdea

group = "io.kotless"
version = "0.1.3"

plugins {
    id("tanvd.kosogor") version "1.0.7" apply true
    id("io.gitlab.arturbosch.detekt").version("1.0.0-RC14") apply true
    kotlin("jvm") version "1.3.61" apply false
}

configureIdea {
    for (example in setOf("kotless-site", "kotless-shortener", "ktor-site", "ktor-shortener").map { "examples/$it" }) {
        exclude += files("$example/.gradle", "$example/gradle", "$example/.idea", "$example/build", "$example/gradlew", "$example/gradlew.bat")
    }
}


subprojects {
    apply {
        plugin("kotlin")
        plugin("tanvd.kosogor")
        plugin("io.gitlab.arturbosch.detekt")
    }

    repositories {
        jcenter()
        gradlePluginPortal()
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            languageVersion = "1.3"
            apiVersion = "1.3"
        }
    }

    detekt {
        parallel = true
        failFast = false
        config = files(File(project.rootProject.projectDir, "buildScripts/detekt/detekt.yml"))
        reports {
            xml {
                enabled = false
            }
            html {
                enabled = false
            }
        }
    }

    afterEvaluate {
        System.setProperty("gradle.publish.key", System.getenv("gradle_publish_key") ?: "")
        System.setProperty("gradle.publish.secret", System.getenv("gradle_publish_secret") ?: "")
    }
}
