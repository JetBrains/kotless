import io.kotless.buildsrc.Versions

group = rootProject.group
//TODO-tanvd Should we align Ktor version with Ktor dsl version
version = rootProject.version


dependencies {
    api(project(":dsl:common:lang-common"))
    api(project(":dsl:ktor:ktor-lang"))

    api("io.ktor", "ktor-server-core", Versions.ktor)
    api("io.ktor", "ktor-server-host-common", Versions.ktor)
    implementation("com.microsoft.azure.functions", "azure-functions-java-library", "1.2.2") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("ch.qos.logback", "logback-classic", Versions.logback)
}
