import io.kotless.buildsrc.Versions

group = rootProject.group
version = rootProject.version

dependencies {
    api(project(":dsl:common:lang-common"))
    api(project(":dsl:kotless:kotless-lang"))

    implementation(kotlin("reflect"))
    implementation("org.reflections", "reflections", "0.9.11")

    implementation("ch.qos.logback", "logback-classic", io.kotless.buildsrc.Versions.logback)
}
