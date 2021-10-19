import io.kotless.buildsrc.Versions

group = rootProject.group
version = rootProject.version

dependencies {
    api(project(":dsl:common:lang-common"))
    api(project(":dsl:kotless:kotless-lang"))

    implementation(kotlin("reflect"))
    implementation("org.reflections", "reflections", "0.9.11")
    implementation("com.microsoft.azure.functions", "azure-functions-java-library", "1.2.2") {
        exclude("org.slf4j", "slf4j-api")
    }

    implementation("ch.qos.logback", "logback-classic", Versions.logback)
}
