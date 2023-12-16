import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "io.kotless.examples"
version = "0.3.0"

plugins {
    kotlin("jvm") version "1.9.21" apply false
}

subprojects {
    apply {
        plugin("kotlin")
    }

    repositories {
        mavenLocal()
        maven(url = uri("https://packages.jetbrains.team/maven/p/ktls/maven"))
        mavenCentral()
        jcenter()
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "17"
            languageVersion = "1.7"
            apiVersion = "1.7"
        }
    }
}
