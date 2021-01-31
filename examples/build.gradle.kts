import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "io.kotless.examples"
version = "0.1.7-beta-5"

plugins {
    id("tanvd.kosogor") version "1.0.10" apply true
    kotlin("jvm") version "1.4.21" apply false
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("tanvd.kosogor")
    }

    repositories {
        mavenLocal()
        jcenter()
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "11"
            languageVersion = "1.4"
            apiVersion = "1.4"
        }
    }
}
