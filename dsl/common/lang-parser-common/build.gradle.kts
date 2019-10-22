import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

dependencies {
    api(project(":schema"))
    api(project(":dsl:common:lang-common"))

    implementation(kotlin("reflect"))
    implementation(kotlin("compiler-embeddable"))
}

publishJar {
    bintray {
        username = "tanvd"
        repository = "io.kotless"
        info {
            description = "Kotless Parser Utils"
            githubRepo = "https://github.com/JetBrains/kotless"
            vcsUrl = "https://github.com/JetBrains/kotless"
            labels.addAll(listOf("kotlin", "serverless", "web", "devops", "faas", "lambda"))
        }
    }
}
