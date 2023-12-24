package io.kotless.plugin.gradle.graal.utils

object GraalSettings {
    const val DEFAULT_REFLECT_FILE_NAME = "reflect.json"
    const val GRAAL_VM_DOCKER_IMAGE = "ghcr.io/graalvm/graalvm-community:21"

    val BASE_GRAAL_FLAGS = listOf(
        "--enable-url-protocols=https",
        "-Djava.net.preferIPv4Stack=true",
        "--no-server",
        "-jar"
    )
    val FULL_GRAAL_VM_FLAGS = listOf(
        "-H:+UnlockExperimentalVMOptions",
        "-H:+ReportExceptionStackTraces",
        "-H:+ReportUnsupportedElementsAtRuntime",
        "-H:+AllowIncompleteClasspath",
        "-H:ReflectionConfigurationFiles=/working/build/$DEFAULT_REFLECT_FILE_NAME",
        "--initialize-at-build-time=io.ktor,kotlinx,kotlin,org.apache.logging.log4j,org.apache.logging.slf4j,org.apache.log4j"
    ) + BASE_GRAAL_FLAGS
}
