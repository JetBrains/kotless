import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "io.kotless.examples"
version = "0.1.5"

plugins {
    id("tanvd.kosogor") version "1.0.9" apply true
    kotlin("jvm") version "1.3.72" apply false
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("tanvd.kosogor")
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
}
