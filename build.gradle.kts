import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "io.kotless"
version = "0.3.0"

plugins {
    id("io.gitlab.arturbosch.detekt") version ("1.15.0") apply true
    kotlin("jvm") version "1.9.21" apply false
    `maven-publish`
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("maven-publish")
        plugin("io.gitlab.arturbosch.detekt")
    }

    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven(url = uri("https://packages.jetbrains.team/maven/p/ktls/maven"))
    }

    val sourceSets = this.extensions.getByName("sourceSets") as SourceSetContainer


    task<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets["main"]!!.allSource)
    }

    publishing {
        publications {
            this.create("jarPublication", MavenPublication::class.java) {
                artifactId = project.name

                from (project.components.getByName("java"))

                artifact(tasks["sourcesJar"])
            }
        }

        repositories {
            maven {
                name = "SpacePackages"
                url = uri("https://packages.jetbrains.team/maven/p/ktls/maven")

                credentials {
                    username = System.getenv("JB_SPACE_CLIENT_ID")
                    password = System.getenv("JB_SPACE_CLIENT_SECRET")
                }
            }
        }
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "17"
            languageVersion = "1.7"
            apiVersion = "1.7"

            freeCompilerArgs = freeCompilerArgs
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
