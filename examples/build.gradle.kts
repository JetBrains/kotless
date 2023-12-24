import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

group = "io.kotless.examples"
version = "0.3.2"

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
            jvmTarget = "21"
            languageVersion = "2.1"
            apiVersion = "2.1"
        }
    }
}
