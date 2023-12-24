group = rootProject.group
version = rootProject.version


dependencies {
    api(kotlin("stdlib"))

    api("org.slf4j", "slf4j-api", "1.7.30")
    api("com.amazonaws:aws-lambda-java-events:3.1.1")
    api("com.amazonaws:aws-lambda-java-core:1.2.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.0")
}
