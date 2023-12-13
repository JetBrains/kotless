import io.kotless.buildsrc.Versions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = rootProject.group
version = rootProject.version


dependencies {
    api(project(":dsl:ktor:ktor-lang"))
    api(project(":dsl:ktor:cloud:ktor-lang-aws"))
    api(project(":dsl:ktor:cloud:ktor-lang-azure"))

    api("io.ktor", "ktor-server-netty", Versions.ktor)
}

tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs
    }
}
