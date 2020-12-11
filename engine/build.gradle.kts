import io.kotless.buildsrc.optInInternalAPI
import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(project(":schema"))
    implementation(kotlin("reflect"))
    implementation ("io.terraformkt:entities:0.1.4")
    implementation ("io.terraformkt.providers:aws:3.14.1-0.1.4")
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


optInInternalAPI()
