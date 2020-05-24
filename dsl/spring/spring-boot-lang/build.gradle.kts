import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version


dependencies {
    api(project(":dsl:common:lang-common"))

    api("com.amazonaws.serverless", "aws-serverless-java-container-springboot2", "1.5")

    api("org.springframework.boot","spring-boot-starter-web", "2.3.0.RELEASE") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }

    implementation("org.slf4j", "slf4j-log4j12", "1.7.25")
    implementation("log4j", "log4j", "1.2.17")
    implementation("com.amazonaws", "aws-lambda-java-log4j", "1.0.0")
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

