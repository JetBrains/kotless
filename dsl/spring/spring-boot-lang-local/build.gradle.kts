import io.kotless.buildsrc.Versions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = rootProject.group
version = rootProject.version


dependencies {
    api(project(":dsl:spring:spring-boot-lang"))
    implementation(kotlin("reflect"))

    api("org.springframework.boot", "spring-boot-starter-tomcat", Versions.springBoot)
}
tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xuse-experimental=io.kotless.InternalAPI")
    }
}
