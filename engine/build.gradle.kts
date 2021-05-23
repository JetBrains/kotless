import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(project(":schema"))
    implementation(kotlin("reflect"))
    implementation("io.terraformkt:entities:0.1.4")
    implementation("io.terraformkt.providers:aws:3.14.1-0.1.4")
    implementation("io.terraformkt.providers:azure:2.35.0-0.1.4")
}


tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xuse-experimental=io.kotless.InternalAPI")
    }
}
