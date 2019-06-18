import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

buildscript {
    repositories { jcenter() }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.3.31")
    }
}

plugins {
    id("kotlinx-serialization") version "1.3.31" apply true
}

dependencies {
    compile(kotlin("stdlib"))

    compile("org.jetbrains.kotlinx", "kotlinx-serialization-runtime", "0.11.0")


    compile(kotlin("reflect"))
    compile("org.reflections", "reflections", "0.9.11")

    compile(project(":model"))

    compile("com.amazonaws", "aws-lambda-java-core", "1.2.0")

    compile("org.slf4j", "slf4j-log4j12", "1.7.25")
    compile("log4j", "log4j", "1.2.17")
    compile("com.amazonaws", "aws-lambda-java-log4j", "1.0.0")
}

publishJar {
    bintray {
        username = "tanvd"
        repository = "io.kotless"
        info {
            description = "Kotless DSL"
            githubRepo = "https://github.com/JetBrains/kotless"
            vcsUrl = "https://github.com/JetBrains/kotless"
            labels.addAll(listOf("kotlin", "serverless", "web", "devops", "faas", "lambda"))
        }
    }
}

