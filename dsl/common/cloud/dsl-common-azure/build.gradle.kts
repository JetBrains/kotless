group = rootProject.group
version = rootProject.version


plugins {
    kotlin("plugin.serialization") version "1.4.21" apply true
}

dependencies {
    api(project(":dsl:common:dsl-common"))

    api("com.microsoft.azure.functions", "azure-functions-java-library", "1.2.2")
}
