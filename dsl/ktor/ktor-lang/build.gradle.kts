import io.kotless.buildsrc.Versions

group = rootProject.group
//TODO-tanvd Should we align Ktor version with Ktor dsl version
version = rootProject.version


dependencies {
    api(project(":dsl:common:lang-common"))

    api("io.ktor", "ktor-server-core", Versions.ktor)
    api("io.ktor", "ktor-server-host-common", Versions.ktor)
    implementation("ch.qos.logback", "logback-classic", Versions.logback)
}
