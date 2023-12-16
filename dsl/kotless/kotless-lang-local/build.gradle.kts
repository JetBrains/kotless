import io.kotless.buildsrc.Versions
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

group = rootProject.group
version = rootProject.version


dependencies {
    api(project(":dsl:kotless:kotless-lang"))
    api(project(":dsl:kotless:cloud:kotless-lang-aws"))

    implementation("org.quartz-scheduler", "quartz", Versions.quartz)
    implementation("org.eclipse.jetty", "jetty-server", "9.4.29.v20200521")
}


tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs
    }
}
