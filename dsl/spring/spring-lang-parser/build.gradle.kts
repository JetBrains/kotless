import io.kotless.buildsrc.Versions
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

group = rootProject.group
version = rootProject.version

dependencies {
    api(project(":schema"))

    api("org.springframework", "spring-web", Versions.spring)

    api(project(":dsl:common:dsl-parser-common"))
}

tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs
    }
}
