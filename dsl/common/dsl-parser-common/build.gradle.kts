import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

group = rootProject.group
version = rootProject.version

dependencies {
    api(kotlin("reflect"))
    api(kotlin("compiler-embeddable"))

    api(project(":schema"))

    api(project(":dsl:common:dsl-common"))
    api(project(":dsl:common:cloud:dsl-common-aws"))
    api(project(":dsl:common:cloud:dsl-common-azure"))

    implementation(project(":dsl:kotless:kotless-lang"))
    implementation(project(":dsl:kotless:cloud:kotless-lang-aws"))
    implementation(project(":dsl:kotless:cloud:kotless-lang-azure"))
}

tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs
    }
}
