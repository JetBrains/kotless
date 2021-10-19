import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = rootProject.group
version = rootProject.version

dependencies {
    api(kotlin("reflect"))
    api(kotlin("compiler-embeddable"))

    api(project(":schema"))
    api(project(":dsl:common:lang-common"))

    implementation(project(":dsl:kotless:kotless-lang"))
    implementation(project(":dsl:kotless:kotless-lang-aws"))
    implementation(project(":dsl:kotless:kotless-lang-azure"))
}

tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xuse-experimental=io.kotless.InternalAPI")
    }
}
