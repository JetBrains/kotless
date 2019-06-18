import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

dependencies {
    compile(kotlin("stdlib"))
    compile(project(":schema"))
}

publishJar {
    bintray {
        username = "tanvd"
        repository = "io.kotless"
        info {
            description = "Kotless Engine"
            githubRepo = "https://github.com/JetBrains/kotless"
            vcsUrl = "https://github.com/JetBrains/kotless"
            labels.addAll(listOf("kotlin", "serverless", "web", "devops", "faas", "lambda"))
        }
    }
}
