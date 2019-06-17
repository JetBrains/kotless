import tanvd.kosogor.proxy.publishJar
import tanvd.kosogor.proxy.publishPlugin

group = rootProject.group
version = rootProject.version

publishPlugin {
    id = "io.kotless"
    displayName = "kotless"
    implementationClass = "io.kotless.plugin.gradle.KotlessPlugin"
    version = project.version.toString()
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(gradleKotlinDsl())

    compile(kotlin("stdlib"))
    compile(kotlin("reflect"))
    compile(kotlin("compiler-embeddable"))

    compile("com.github.jengelman.gradle.plugins", "shadow", "5.0.0")

    compile("org.codehaus.plexus", "plexus-utils", "3.1.1")
    compile("org.codehaus.plexus", "plexus-archiver", "4.1.0")
    compile("org.codehaus.plexus", "plexus-container-default", "1.0-alpha-30")

    compile(project(":ktls-dsl:lang-parser"))
    compile(project(":engine"))
}
