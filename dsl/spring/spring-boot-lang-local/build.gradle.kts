import io.kotless.buildsrc.Versions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = rootProject.group
version = rootProject.version


dependencies {
    api(project(":dsl:spring:spring-boot-lang"))
    implementation(kotlin("reflect"))

    api("org.springframework.boot", "spring-boot-starter-tomcat", Versions.springBoot) {
        exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
    }
}

tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xuse-experimental=io.kotless.InternalAPI")
    }
}
