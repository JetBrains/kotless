import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "io.kotless"
version = "0.1.0-SNAPSHOT"

plugins {
    id("tanvd.kosogor") version "1.0.5" apply true
    id("io.gitlab.arturbosch.detekt").version("1.0.0-RC14") apply true
    kotlin("jvm") version "1.3.31" apply false
}


subprojects {
    apply {
        plugin("kotlin")
        plugin("tanvd.kosogor")
        plugin("io.gitlab.arturbosch.detekt")
    }

    repositories {
        jcenter()
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
}
