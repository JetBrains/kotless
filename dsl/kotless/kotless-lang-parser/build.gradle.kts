import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

group = rootProject.group
version = rootProject.version

dependencies {
    api(project(":schema"))
    api(project(":dsl:kotless:kotless-lang"))
    api(project(":dsl:common:dsl-parser-common"))
}


tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs
    }
}
