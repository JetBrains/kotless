import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = rootProject.group
version = rootProject.version

dependencies {
    api(project(":schema"))
    api(project(":dsl:kotless:kotless-lang"))
    api(project(":dsl:common:lang-parser-common"))
}

tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xuse-experimental=io.kotless.InternalAPI")
    }
}
