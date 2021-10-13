import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import tanvd.kosogor.defaults.configureIdea

group = "io.kotless"
version = "0.2.0"

plugins {
    id("tanvd.kosogor") version "1.0.12" apply true
//    id("io.gitlab.arturbosch.detekt") version ("1.15.0") apply true
    kotlin("jvm") version "1.5.30" apply false
}

configureIdea {
    exclude += files("examples/.gradle", "examples/gradle", "examples/.idea", "examples/build", "examples/gradlew", "examples/gradlew.bat")
}


subprojects {
    apply {
        plugin("kotlin")
        plugin("tanvd.kosogor")
//        plugin("io.gitlab.arturbosch.detekt")
    }

    repositories {
        mavenLocal()
        jcenter()
        gradlePluginPortal()
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "11"
            languageVersion = "1.5"
            apiVersion = "1.5"

            useIR = true

            freeCompilerArgs = freeCompilerArgs + listOf("-Xuse-experimental=kotlin.Experimental")
        }
    }

//    detekt {
//        parallel = true
//
//        config = rootProject.files("detekt.yml")
//
//        reports {
//            xml {
//                enabled = false
//            }
//            html {
//                enabled = false
//            }
//        }
//    }

    afterEvaluate {
        System.setProperty("gradle.publish.key", System.getenv("gradle_publish_key") ?: "")
        System.setProperty("gradle.publish.secret", System.getenv("gradle_publish_secret") ?: "")
    }
}
