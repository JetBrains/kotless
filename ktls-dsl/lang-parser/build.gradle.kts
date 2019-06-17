import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

dependencies {
    compile(project(":ktls-dsl:lang"))
    compile(project(":schema"))
    compile(kotlin("compiler-embeddable"))
}
