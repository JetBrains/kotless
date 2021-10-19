import io.kotless.buildsrc.Versions

group = rootProject.group
version = rootProject.version


dependencies {
    api(project(":model"))
    api(project(":dsl:common:lang-common"))
    implementation(kotlin("reflect"))
    implementation("org.reflections", "reflections", "0.9.11")

    implementation("ch.qos.logback", "logback-classic", Versions.logback)
}
