group = rootProject.group
//TODO-tanvd Should we align Ktor version with Ktor dsl version
version = rootProject.version


dependencies {
    api(project(":dsl:common:cloud:dsl-common-aws"))
    api(project(":dsl:ktor:ktor-lang"))
}
