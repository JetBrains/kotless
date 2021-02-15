import io.kotless.buildsrc.Versions
import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version


dependencies {
    api(project(":dsl:common:lang-common"))
    implementation("com.microsoft.azure.functions", "azure-functions-java-library", "1.2.2")

    implementation(kotlin("reflect"))
    implementation("org.reflections", "reflections", "0.9.11")

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

