import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version


dependencies {
    api(project(":dsl:common:lang-common"))

    api("io.ktor", "ktor-server-core", "1.2.5")
    api("io.ktor", "ktor-server-host-common", "1.2.5")

    implementation("org.slf4j", "slf4j-log4j12", "1.7.25")
    implementation("log4j", "log4j", "1.2.17")
    implementation("com.amazonaws", "aws-lambda-java-log4j", "1.0.0")
}

publishJar {
    bintray {
        username = "tanvd"
        repository = "io.kotless"
        info {
            description = "Ktor DSL"
            githubRepo = "https://github.com/JetBrains/kotless"
            vcsUrl = "https://github.com/JetBrains/kotless"
            labels.addAll(listOf("kotlin", "serverless", "web", "devops", "faas", "lambda"))
        }
    }
}

