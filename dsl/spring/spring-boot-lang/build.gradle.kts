import io.kotless.buildsrc.Versions

group = rootProject.group
version = rootProject.version


dependencies {
    api(project(":dsl:common:cloud:dsl-common-aws"))

    api("com.amazonaws.serverless", "aws-serverless-java-container-springboot2", Versions.serverlessContainers) {
        exclude("org.slf4j", "slf4j-api")
    }

    api("org.springframework.boot", "spring-boot-starter-web", Versions.springBoot) {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
}
