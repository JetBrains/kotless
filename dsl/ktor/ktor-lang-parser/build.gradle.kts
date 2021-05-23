import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = rootProject.group
version = rootProject.version

dependencies {
    api(project(":schema"))
    api(project(":dsl:ktor:ktor-lang"))
    api(project(":dsl:common:dsl-parser-common"))
    api(project(":dsl:ktor:cloud:ktor-lang-azure"))
    api(project(":dsl:ktor:cloud:ktor-lang-aws"))
}


tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xuse-experimental=io.kotless.InternalAPI")
    }
}
