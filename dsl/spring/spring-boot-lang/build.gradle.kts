import io.kotless.buildsrc.Versions
import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version


dependencies {
    api(project(":dsl:common:lang-common"))

    api("com.amazonaws.serverless", "aws-serverless-java-container-springboot2", Versions.serverlessContainers) {
        exclude("org.slf4j", "slf4j-api")
    }

    api("org.springframework.boot", "spring-boot-starter-web", Versions.springBoot) {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
}

publishJar {
    bintray {
        username = "tanvd"
        repository = "io.kotless"
        info {
            description = "Spring DSL"
            githubRepo = "https://github.com/JetBrains/kotless"
            vcsUrl = "https://github.com/JetBrains/kotless"
            labels.addAll(listOf("kotlin", "serverless", "web", "devops", "faas", "lambda", "spring"))
        }
    }
}

