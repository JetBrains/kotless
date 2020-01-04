import tanvd.kosogor.proxy.publishJar
import tanvd.kosogor.proxy.publishPlugin

group = rootProject.group
version = rootProject.version

publishJar {
    publication {
        artifactId = "io.kotless.gradle.plugin"
    }
}

publishPlugin {
    id = "io.kotless"
    displayName = "kotless"
    implementationClass = "io.kotless.plugin.gradle.KotlessPlugin"
    version = project.version.toString()

    info {
        website = "https://github.com/JetBrains/kotless"
        vcsUrl = "https://github.com/JetBrains/kotless"
        description = "Kotlin Serverless Framework"
        tags.addAll(listOf("kotlin", "serverless", "web", "devops", "faas", "lambda"))
    }
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    implementation(project(":dsl:kotless:lang-parser"))
    implementation(project(":dsl:ktor:ktor-lang-parser"))
    implementation(project(":engine"))

    implementation("com.github.jengelman.gradle.plugins", "shadow", "5.0.0")

    implementation("com.amazonaws", "aws-java-sdk-core", "1.11.699")
    implementation("org.testcontainers", "localstack", "1.12.3")

    implementation("org.codehaus.plexus", "plexus-utils", "3.3.0")
    implementation("org.codehaus.plexus", "plexus-archiver", "4.2.1")
    implementation("org.codehaus.plexus", "plexus-container-default", "2.1.0")
}
