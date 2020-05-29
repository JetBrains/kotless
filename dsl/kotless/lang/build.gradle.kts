import io.kotless.buildsrc.Versions
import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version


dependencies {
    api(project(":dsl:common:lang-common"))

    implementation(kotlin("reflect"))
    implementation("org.reflections", "reflections", "0.9.12")

    implementation("org.slf4j", "slf4j-log4j12", Versions.slf4j)
    implementation("log4j", "log4j", Versions.log4j)
    implementation("com.amazonaws", "aws-lambda-java-log4j", Versions.lambdaJavaLog4J)
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

