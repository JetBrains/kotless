import io.kotless.buildsrc.Versions
import io.reflekt.plugin.reflekt
import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version


repositories {
    maven(url = uri("https://packages.jetbrains.team/maven/p/reflekt/reflekt"))
}

plugins {
    id("io.reflekt") version "1.5.30" apply true
}

reflekt {
    enabled = true
    toSaveMetadata = true
}

dependencies {
    api(project(":model"))
    api(project(":dsl:common:lang-common"))
    implementation(kotlin("reflect"))
    implementation("io.reflekt", "reflekt-dsl", "1.5.30")
//    implementation("org.reflections", "reflections", "0.9.11")

    implementation("ch.qos.logback", "logback-classic", Versions.logback)
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

