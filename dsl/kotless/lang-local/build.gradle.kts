import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version


dependencies {
    api(project(":dsl:kotless:lang"))

    implementation("org.eclipse.jetty", "jetty-server", "9.4.25.v20191220")
}

publishJar {
    bintray {
        username = "tanvd"
        repository = "io.kotless"
        info {
            description = "Kotless DSL Local Runner"
            githubRepo = "https://github.com/JetBrains/kotless"
            vcsUrl = "https://github.com/JetBrains/kotless"
            labels.addAll(listOf("kotlin", "serverless", "web", "devops", "faas", "lambda"))
        }
    }
}

