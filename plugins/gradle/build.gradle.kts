import io.kotless.buildsrc.Versions
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
    implementation("org.jetbrains.kotlin", "kotlin-gradle-plugin", Versions.kotlin)

    implementation(project(":dsl:kotless:kotless-lang-parser"))
    implementation(project(":dsl:ktor:ktor-lang-parser"))
    implementation(project(":dsl:spring:spring-lang-parser"))
    implementation(project(":engine"))

    implementation("com.github.jengelman.gradle.plugins", "shadow", "6.0.0")

    implementation("com.amazonaws", "aws-java-sdk-core", Versions.aws)
    implementation("org.testcontainers", "localstack", "1.14.3")

    implementation("org.codehaus.plexus", "plexus-utils", "3.3.0")
    implementation("org.codehaus.plexus", "plexus-archiver", "4.2.1")
    implementation("org.codehaus.plexus", "plexus-container-default", "2.1.0")

    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.6.0")
    testImplementation("org.junit.jupiter", "junit-jupiter-params", "5.6.0")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.6.0")
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeTags = setOf("unit")
        excludeTags = setOf("integration")
    }
}

tasks.create("integration", Test::class.java) {
    useJUnitPlatform {
        excludeTags = setOf("unit")
        includeTags = setOf("integration")
    }
}
