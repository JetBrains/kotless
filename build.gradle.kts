import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import tanvd.kosogor.defaults.configureIdea

group = "io.kotless"
version = "0.1.7"

plugins {
    id("tanvd.kosogor") version "1.0.9" apply true
    id("io.gitlab.arturbosch.detekt") version ("1.8.0") apply true
    kotlin("jvm") apply false
}

configureIdea {
    exclude += files("examples/.gradle", "examples/gradle", "examples/.idea", "examples/build", "examples/gradlew", "examples/gradlew.bat")
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

            freeCompilerArgs = freeCompilerArgs + listOf("-Xuse-experimental=kotlin.Experimental")
        }
    }

    detekt {
        parallel = true

        config = rootProject.files("detekt.yml")

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
